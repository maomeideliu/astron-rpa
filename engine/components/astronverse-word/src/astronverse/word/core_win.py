import io
import os
import re
import tempfile
from typing import Tuple

import psutil
from win32api import RGB
import win32clipboard
import win32com.client
from docx import Document
from docx.shared import RGBColor
from PIL import Image
from astronverse.actionlib.logger import logger
from astronverse.actionlib.types import PATH
from astronverse.actionlib.utils import handle_existence

from astronverse.word import (
    SaveType,
    ApplicationType,
    EncodingType,
    SelectRangeType,
    FileExistenceType,
    CloseRangeType,
    ReplaceType,
    ReplaceMethodType,
    SelectTextType,
    CursorPointerType,
    CursorPositionType,
    MoveDirectionType,
    MoveUpDownType,
    MoveLeftRightType,
    InsertionType,
    InsertImgType,
    SearchTableType,
    TableBehavior,
    RowAlignment,
    VerticalAlignment,
    UnderLineStyle,
    DeleteMode,
    CommentType,
    SaveFileType,
    ConvertPageType,
)

from astronverse.word.core import IDocumentCore
from astronverse.word.error import (
    CONTENT_FORMAT_ERROR_FORMAT,
    CLIPBOARD_PASTE_ERROR,
    DOCUMENT_PATH_ERROR_FORMAT,
    TABLE_NOT_EXIST_ERROR,
    FILENAME_ALREADY_EXISTS_ERROR,
)


class WordDocumentCore(IDocumentCore):
    word_application_instance = None

    @staticmethod
    def _is_word_application_running():
        is_word_running, is_wps_running = False, False
        process_ids = psutil.pids()
        for process_id in process_ids:
            process = psutil.Process(process_id)
            if process.name() == "WINWORD.EXE":
                is_word_running = True
            if process.name() == "wps.exe":
                is_wps_running = True
        return is_word_running, is_wps_running

    @staticmethod
    def _save_document(doc, file_path, file_name, save_type, exist_handle_type):
        if save_type == SaveType.SAVE_AS and file_path:
            document_extension = "." + doc.Name.split(".")[-1]  # 获取文件后缀，防止文件名中有点号
            if not file_name:
                file_name = doc.Name.split(document_extension)[0]
            destination_file_path = os.path.join(file_path, file_name + document_extension)
            new_file_path = handle_existence(destination_file_path, exist_handle_type)
            doc.SaveAs(FileName=new_file_path)
            return new_file_path
        if save_type == SaveType.SAVE:
            doc.Save()
            return doc.FullName
        return None

    @staticmethod
    def _create_word_application(params: str):
        try:
            word_application = win32com.client.gencache.EnsureDispatch(params)
            return word_application
        except Exception:
            try:
                word_application = win32com.client.Dispatch(params)
                return word_application
            except Exception:
                logger.debug(f"创建Word对象失败：{params}")
                return None

    @classmethod
    def initialize_word_application(cls, default_application: ApplicationType = ApplicationType.DEFAULT):
        is_word_running, _ = cls._is_word_application_running()
        if default_application == ApplicationType.DEFAULT and is_word_running:
            keys = ["Word.Application", "Kwps.Application", "wps.Application"]
        elif default_application == ApplicationType.DEFAULT:
            keys = ["Kwps.Application", "wps.Application", "Word.Application"]
        elif default_application == ApplicationType.WORD:
            keys = ["Word.Application"]
        elif default_application == ApplicationType.WPS:
            keys = ["Kwps.Application", "wps.Application"]
        else:
            keys = []

        for application_key in keys:
            try:
                cls.word_application_instance = cls._create_word_application(application_key)
                if cls.word_application_instance:
                    return cls.word_application_instance
            except Exception:
                continue
        raise Exception("未检测到所选程序的注册表信息，请检查是否安装！")

    @classmethod
    def open(
        cls,
        document_path: PATH = "",
        preferred_application: ApplicationType = ApplicationType.WORD,
        is_visible: bool = True,
        text_encoding: EncodingType = EncodingType.UTF8,
        open_password: str = "",
        write_password: str = "",
    ) -> object:
        """
        打开Word文件
        :param document_path: word文件路径
        :param preferred_application: 默认打开的应用
        :param is_visible: 是否可见
        :param text_encoding: 编码格式
        :param open_password: 打开密码
        :param write_password: 写入密码
        :return: word对象
        """
        if cls.word_application_instance is None:
            cls.initialize_word_application(preferred_application)
            # cls.word_application_instance.Visible = True
        if is_visible:
            cls.word_application_instance.Visible = True
        if document_path:
            cls.ScreenUpdating = True
            cls.word_application_instance.DisplayAlerts = False
            document = cls.word_application_instance.Documents.Open(
                FileName=document_path,
                PasswordDocument=open_password,
                WritePasswordDocument=write_password,
                Encoding=text_encoding,
                Visible=is_visible,
            )
            cls.word_application_instance.DisplayAlerts = True
            print(document.Name)
        else:
            raise LookupError("没有输入路径，请检查输入的word路径是否正确!")
        return document

    @classmethod
    def read(cls, document: object, content_selection_range=SelectRangeType.ALL):
        """
        读取Word文档内容
        :param document: word对象
        :param content_selection_range: 选择范围
        :return: 文档内容
        """
        if not cls.word_application_instance:
            cls.word_application_instance = document.Application
            cls.word_application_instance.Visible = True
        if content_selection_range == SelectRangeType.SELECTED:
            selection = cls.word_application_instance.Selection
            return re.sub(r"\r", "\n", selection.Text)

        document_content = ""
        for paragraph in document.Paragraphs:
            paragraph_text = re.sub(r"\r", "", paragraph.Range.Text) + "\n"
            document_content += paragraph_text
        return document_content

    @classmethod
    def create(
        cls,
        file_path: str = "",
        file_name: str = "",
        visible_flag: bool = True,
        default_application: ApplicationType = ApplicationType.WORD,
        exist_handle_type: FileExistenceType = FileExistenceType.RENAME,
    ) -> Tuple[object, str]:
        """
        Word - 文档操作 - 创建
        """
        if cls.word_application_instance is None:
            cls.initialize_word_application(default_application)
            cls.word_application_instance.Visible = True
        new_file_path = os.path.join(file_path, file_name)
        new_file_path = handle_existence(new_file_path, exist_handle_type)
        doc = cls.word_application_instance.Documents.Add(Visible=visible_flag)
        if new_file_path:
            doc.SaveAs(FileName=new_file_path)
        return doc, new_file_path

    @classmethod
    def save(
        cls,
        doc: object,
        file_path: str = "",
        file_name: str = "",
        save_type=SaveType.SAVE,
        exist_handle_type: FileExistenceType = FileExistenceType.RENAME,
        close_flag: bool = False,
    ) -> PATH:
        """
        Word - 文档操作 - 保存
        """
        save_file_path = cls._save_document(doc, file_path, file_name, save_type, exist_handle_type)
        if close_flag:
            doc.Close(SaveChanges=0)
        return save_file_path

    @classmethod
    def close(
        cls,
        doc: object,
        file_path: str = "",
        file_name: str = "",
        save_type=SaveType.SAVE,
        exist_handle_type: FileExistenceType = FileExistenceType.RENAME,
        close_range_flag: CloseRangeType = CloseRangeType.ONE,
        pkill_flag: bool = False,
    ):
        """
        Word - 文档操作 - 关闭并保存。如果 save_flag 是 True，需要支持 file_path
        """
        if close_range_flag == CloseRangeType.ALL:
            if pkill_flag:
                try:
                    os.system("taskkill /f /im wps.exe")
                    os.system("taskkill /f /im WINWORD.EXE")
                except Exception:
                    pass
            else:
                cls.word_application_instance.Quit()
            cls.word_application_instance = None
        else:
            cls._save_document(doc, file_path, file_name, save_type, exist_handle_type)
            try:
                doc.Close(SaveChanges=0)
            except Exception as e:
                raise e

    @classmethod
    def insert(
        cls,
        doc: object,
        text: str = "",
        enter_flag: bool = False,
        text_format: dict = None,
    ):
        doc.Activate()
        selection = doc.Application.Selection
        if enter_flag:
            selection.TypeParagraph()
        selection.TypeText(text)
        selection.Start = selection.Start - len(text)  # 将光标移回到插入的文字开始位置 32
        selection.End = selection.End  # 将光标移到插入的文字结束位置
        # 设置文字格式（例如加粗、斜体等）
        selection.Font.Bold = text_format["bold"]
        selection.Font.Italic = text_format["italic"]
        selection.Font.Underline = text_format["underline"].value
        # 设置文字字体
        selection.Font.Name = text_format["font_name"]
        # 设置文字大小
        selection.Font.Size = text_format["font_size"]
        rgb_color = text_format["font_color"].split(",")
        selection.Font.Color = RGB(int(rgb_color[0]), int(rgb_color[1]), int(rgb_color[2]))
        selection.Start = selection.End  # 将光标移到末尾

    @classmethod
    def replace(
        cls,
        doc: object,
        replace_type: ReplaceType = ReplaceType.STR,
        origin_word: str = "",
        new_word: str = "",
        img_path: str = "",
        replace_method: ReplaceMethodType = ReplaceMethodType.ALL,
        ignore_case: bool = True,
    ):
        """
        Word - 文档内容 - 替换
        params:replace_type:替换类型，str/img
                origin_word:原始文字/图片路径
                new_word:新文字/图片路径
                img_path:图片路径
                replace_method:替换方法，first/all
        params:ignore_case:忽略大小写， True/False

        https://learn.microsoft.com/zh-hk/office/vba/api/word.find.execute
        wdReplaceAll	2	取代所有項目。
        wdReplaceNone	0	不取代任何項目。
        wdReplaceOne	1	取代第一個出現的項目。
        """
        doc.Activate()
        selection = doc.Application.Selection
        replace_count = 0
        wdReplaceIndex = 2 if replace_method == ReplaceMethodType.ALL else 1
        if replace_method == ReplaceMethodType.FIRST:
            found = selection.Find
            found.ClearFormatting()
            found.Text = origin_word
            match_case = not ignore_case
            found.MatchCase = match_case
            found.Execute()
            if replace_type == ReplaceType.STR:
                doc_range = doc.Content
                doc_range.Find.Execute(
                    origin_word,
                    match_case,
                    False,
                    False,
                    False,
                    False,
                    True,
                    1,
                    True,
                    new_word,
                    wdReplaceIndex,
                )
            else:
                selection.InlineShapes.AddPicture(img_path)
                selection.TypeBackspace()
            replace_count = 1
        elif replace_method == ReplaceMethodType.ALL:
            selection.HomeKey(Unit=6)
            found = selection.Find
            found.ClearFormatting()
            found.Text = origin_word
            match_case = not ignore_case
            found.MatchCase = match_case
            if replace_type == ReplaceType.STR:
                doc_range = doc.Content
                doc_range.Find.Execute(
                    origin_word,
                    match_case,
                    False,
                    False,
                    False,
                    False,
                    True,
                    1,
                    True,
                    new_word,
                    wdReplaceIndex,
                )
                while found.Execute():
                    replace_count += 1
            else:
                while found.Execute():
                    range_obj = found.Parent
                    range_obj.Text = ""
                    range_obj.InlineShapes.AddPicture(img_path)
                    replace_count += 1

        return replace_count

    @classmethod
    def select(
        cls,
        doc: object,
        select_type: SelectTextType = SelectTextType.ALL,
        p_start: int = 1,
        p_end: int = 1,
        r_start: int = 1,
        r_end: int = 1,
    ):
        doc.Activate()
        s = doc.Application.Selection
        if select_type == SelectTextType.ALL:
            s.WholeStory()
        elif select_type == SelectTextType.ROW:
            s.GoTo(3, 1, r_start)
            drift_num = r_end - r_start + 1
            s.MoveDown(5, drift_num, 1)
            s.EndOf(5, 1)
        elif select_type == SelectTextType.PARAGRAPH:
            s.SetRange(
                Start=doc.Paragraphs(p_start).Range.Start,
                End=doc.Paragraphs(p_end).Range.End,
            )
            s.Select()

    @classmethod
    def cursor_position(
        cls,
        doc,
        by: CursorPointerType = CursorPointerType.ALL,
        pos: CursorPositionType = CursorPositionType.HEAD,
        content: str = "",
        c_idx: int = 1,
        p_idx: int = 1,
        r_idx: int = 1,
    ):
        doc.Activate()
        s = doc.Application.Selection
        if by == CursorPointerType.CONTENT:  # 按照文本定位
            if not content:
                raise BaseException(
                    CONTENT_FORMAT_ERROR_FORMAT,
                    "请填写要定位光标的文本内容,目前不支持空内容的定位!!!",
                )
            try:
                s.GoTo(3, 1, 1)  # 移动到文档第一行最开始的地方
                for _ in range(
                    c_idx
                ):  # 找第几个,就需要将这个函数循环几次;比如查找文档中第3个"文言文"这个关键词,需要循环3次
                    s.Find.Execute(FindText=content, Forward=True, MatchCase=True)
                if pos == CursorPositionType.HEAD:  # 将光标定位到关键词开头
                    s.SetRange(Start=s.Start, End=s.Start)
                else:
                    s.SetRange(Start=s.End, End=s.End)
            except Exception as e:
                raise BaseException(CONTENT_FORMAT_ERROR_FORMAT, "内容不存在！") from e
        elif by == CursorPointerType.ALL:  # 按照文档定位光标
            try:
                p_num = doc.Paragraphs.Count  # 获取全部段落号
                if pos == CursorPositionType.TAIL:  # 移动整个文档末尾
                    s.Move(4, p_num)
                else:  # 移动到整个文档开头
                    s.Move(4, -p_num)
            except Exception as e:
                raise BaseException(CONTENT_FORMAT_ERROR_FORMAT, "文档为空！") from e
        elif by == CursorPointerType.PARAGRAPH:  # 按照段落号定位光标
            if pos == CursorPositionType.TAIL:  # 定位到某个段落末尾
                s.SetRange(
                    Start=doc.Paragraphs(p_idx).Range.End - 1,
                    End=doc.Paragraphs(p_idx).Range.End - 1,
                )
            elif pos == CursorPositionType.HEAD:  # 定位到某个段落开头
                s.SetRange(
                    Start=doc.Paragraphs(p_idx).Range.Start,
                    End=doc.Paragraphs(p_idx).Range.Start,
                )
            else:
                raise BaseException(
                    CONTENT_FORMAT_ERROR_FORMAT,
                    "不支持的参考位置，请前端检查传入的p_pos参数！",
                )
        elif by == CursorPointerType.ROW:
            try:
                if pos == CursorPositionType.HEAD:  # 定位到行首
                    s.GoTo(3, 1, r_idx)
                elif pos == CursorPositionType.TAIL:  # 定位到行尾
                    s.GoTo(3, 1, r_idx)
                    s.EndKey(5)
            except Exception as e:
                raise BaseException(CONTENT_FORMAT_ERROR_FORMAT, "内容为空！") from e

    @classmethod
    def move_cursor(
        cls,
        doc: object = None,
        direction: MoveDirectionType = MoveDirectionType.UP,
        unitupdown: MoveUpDownType = MoveUpDownType.ROW,
        unitleftright: MoveLeftRightType = MoveLeftRightType.CHARACTER,
        distance: int = 0,
        with_shift: bool = False,
    ):
        doc.Activate()
        s = doc.Application.Selection
        if direction == MoveDirectionType.UP:
            unit = 5 if unitupdown == MoveUpDownType.ROW else 4
            if with_shift:
                s.MoveUp(unit, distance, 1)
            else:
                s.MoveUp(unit, distance)
        elif direction == MoveDirectionType.DOWN:
            unit = 5 if unitupdown == MoveUpDownType.ROW else 4
            if with_shift:
                s.MoveDown(unit, distance, 1)
            else:
                s.MoveDown(unit, distance)
        elif direction == MoveDirectionType.LEFT:
            unit = 1 if unitleftright == MoveLeftRightType.CHARACTER else 2
            if with_shift:
                s.MoveLeft(unit, distance, 1)
            else:
                s.MoveLeft(unit, distance)
        elif direction == MoveDirectionType.RIGHT:
            unit = 1 if unitleftright == MoveLeftRightType.CHARACTER else 2
            if with_shift:
                s.MoveRight(unit, distance, 1)
            else:
                s.MoveRight(unit, distance)
        else:
            raise BaseException(
                CONTENT_FORMAT_ERROR_FORMAT,
                "不支持的direction，请前端检查传入的direction参数！",
            )

    @classmethod
    def insert_sep(cls, doc: object = None, sep_type: InsertionType = InsertionType.PARAGRAPH):
        doc.Activate()
        s = doc.Application.Selection
        if sep_type == InsertionType.PAGE:
            s.InsertNewPage()
        elif sep_type == InsertionType.PARAGRAPH:
            s.InsertParagraph()
        else:
            raise BaseException(
                CONTENT_FORMAT_ERROR_FORMAT,
                "不支持的分隔符类型，请前端检查传入的sep_type参数！！！",
            )

    @classmethod
    def insert_hyperlink(cls, doc: object = None, url: str = "", display: str = ""):
        doc.Activate()
        s = doc.Application.Selection
        start = s.Start
        s.TypeText(url)
        end = s.End
        s.SetRange(Start=start, End=end)
        if display:
            doc.Hyperlinks.Add(Anchor=s.Range, Address=url, TextToDisplay=display)
        else:
            doc.Hyperlinks.Add(Anchor=s.Range, Address=url, TextToDisplay=url)

    @classmethod
    def insert_img(
        cls,
        doc,
        img_from: InsertImgType = InsertImgType.FILE,
        img_path: str = "",
        scale: int = 100,
        newline: bool = False,
    ):
        doc.Activate()
        s = doc.Application.Selection
        if newline:
            s.TypeParagraph()
        if img_from == InsertImgType.CLIPBOARD:
            # 检查剪贴板是否有图片数据
            win32clipboard.OpenClipboard()
            if win32clipboard.IsClipboardFormatAvailable(win32clipboard.CF_HDROP):
                file_paths = win32clipboard.GetClipboardData(win32clipboard.CF_HDROP)
                for file_path in file_paths:
                    if file_path.lower().endswith((".png", ".jpg", ".jpeg")):
                        img_shape = s.InlineShapes.AddPicture(file_path)
                        img_shape.ScaleWidth = scale
                        img_shape.ScaleHeight = scale
                        return
            # 检查剪贴板中是否有位图数据
            if win32clipboard.IsClipboardFormatAvailable(win32clipboard.CF_DIB):
                img = win32clipboard.GetClipboardData(win32clipboard.CF_DIB)
            elif win32clipboard.IsClipboardFormatAvailable(win32clipboard.CF_BITMAP):
                img = win32clipboard.GetClipboardData(win32clipboard.CF_BITMAP)
            elif win32clipboard.IsClipboardFormatAvailable(win32clipboard.CF_DIBV5):
                img = win32clipboard.GetClipboardData(win32clipboard.CF_DIBV5)
            else:
                win32clipboard.CloseClipboard()
                raise BaseException(CLIPBOARD_PASTE_ERROR.format("剪贴板没有图片数据"), "")
            # 将字节数据转换为Image对象
            image = Image.open(io.BytesIO(img))
            if image.mode != "RGB":
                image = image.convert("RGB")
            with tempfile.NamedTemporaryFile(delete=False, suffix=".png") as temp_file:
                temp_file_path = temp_file.name
                print("临时文件路径:", temp_file_path)
                # 将字节数据转换为Image对象
                image.save(temp_file_path, "PNG")
                img_shape = s.InlineShapes.AddPicture(temp_file_path)
                img_shape.ScaleWidth = scale
                img_shape.ScaleHeight = scale

                os.remove(temp_file_path)
            win32clipboard.CloseClipboard()

        else:
            img_shape = s.InlineShapes.AddPicture(img_path)
            if not os.path.isfile(img_path):
                raise BaseException(DOCUMENT_PATH_ERROR_FORMAT.format(img_path), "图片路径错误")
        img_shape.ScaleWidth = scale
        img_shape.ScaleHeight = scale

    @classmethod
    def read_table(
        cls,
        doc: object,
        search_type: SearchTableType = SearchTableType.IDX,
        idx: int = 1,
        text: str = "",
    ):
        doc.Activate()
        table_content = []

        if search_type == SearchTableType.IDX:
            try:
                # 获取第 idx 个表格
                table = doc.Tables(idx)
                table_content = cls._extract_table_content(table)
            except Exception as e:
                raise BaseException(TABLE_NOT_EXIST_ERROR.format("序号" + str(idx))) from e
        elif search_type == SearchTableType.TEXT:
            # 遍历所有表格，查找包含指定文本的表格
            count = 0
            for table in doc.Tables:
                if any(text in cell.Range.Text for row in table.Rows for cell in row.Cells):
                    count += 1
                    if count == idx:
                        table_content = cls._extract_table_content(table)
                        return table_content
            if not table_content:
                raise BaseException(TABLE_NOT_EXIST_ERROR.format("内容" + str(text)))

        return table_content

    @classmethod
    def insert_table(
        cls,
        doc: object,
        table_content: list = "",
        table_behavior: TableBehavior = TableBehavior.DEFAULT,
        alignment: RowAlignment = RowAlignment.LEFT,
        v_alignment: VerticalAlignment = VerticalAlignment.TOP,
        border: bool = True,
        if_change_font: bool = False,
        font_size=None,
        font_color=None,
        font_set=None,
        font_bold: bool = False,
        font_italic: bool = False,
        underline: UnderLineStyle = UnderLineStyle.DEFAULT,
        newline: bool = True,
    ):
        rows = len(table_content)
        cols = len(table_content[0]) if rows > 0 else 0
        doc.Activate()
        selection_range = doc.Application.Selection.Range
        # 插入表格
        table = doc.Tables.Add(selection_range, NumRows=rows, NumColumns=cols)
        table.AutoFitBehavior(table_behavior == TableBehavior.AUTO)

        for row_idx, row_data in enumerate(table_content):
            row = table.Rows.Item(row_idx + 1)
            for col_idx, cell_data in enumerate(row_data):
                cell = row.Cells.Item(col_idx + 1)
                cell.Range.Text = cell_data
                cell.Range.Paragraphs.Alignment = alignment.value
                cell.VerticalAlignment = v_alignment.value
                if if_change_font:
                    # 设置字体属性
                    run = cell.Range.Font
                    run.Name = font_set if font_set else "宋体"
                    run.Size = font_size if font_size else 12
                    run.Bold = font_bold
                    run.Italic = font_italic
                    run.Underline = underline.value if underline else 0
                    if font_color:
                        run.Color = RGBColor(*font_color).rgb

                    # 获取或添加 rPr 元素
                    # rPr = run._element.get_or_add_rPr()
                    # rFonts = rPr.find(qn('w:rFonts'))
                    # if rFonts is None:
                    #     rFonts = OxmlElement('w:rFonts')
                    #     rPr.append(rFonts)
                    # rFonts.set(qn('w:eastAsia'), font_set if font_set else '宋体')
                if newline:
                    cell.Range.Text += "\n"

            # Set table border style
        if border:
            tblBorders = table.Borders
            tblBorders.InsideLineStyle = 1  # 实线
            tblBorders.OutsideLineStyle = 1  # 实线
            tblBorders.InsideLineWidth = 4
            tblBorders.OutsideLineWidth = 4
            tblBorders.InsideColor = 0x000000  # 黑色
            tblBorders.OutsideColor = 0x000000  # 黑色

    @classmethod
    def delete(
        cls,
        doc: object,
        delete_mode: DeleteMode = DeleteMode.ALL,
        delete_str: str = "",
        delete_idx: int = 0,
        str_delete_all: bool = False,
        p_start: int = 0,
        c_start: int = 0,
        p_end: int = 0,
        c_end: int = 0,
    ):
        doc.Activate()
        if delete_mode == DeleteMode.ALL:
            # 删除所有内容
            doc.Content.Delete()
        elif delete_mode == DeleteMode.CONTENT:
            # 删除第{delete_idx}个内容为{delete_str}的元素
            if str_delete_all:
                # 删除所有内容为{delete_str}的元素
                for paragraph in doc.Paragraphs:
                    if delete_str in paragraph.Range.Text:
                        paragraph.Range.Text = paragraph.Range.Text.replace(delete_str, "")
            else:
                count = 0
                for paragraph in doc.Paragraphs:
                    if delete_str in paragraph.Range.Text:
                        text = paragraph.Range.Text
                        start_pos = text.find(delete_str)
                        while start_pos != -1:
                            count += 1
                            if count == delete_idx:
                                end_pos = start_pos + len(delete_str)
                                paragraph.Range.Text = text[:start_pos] + text[end_pos:]
                                break
                            start_pos = text.find(delete_str, start_pos + len(delete_str))
        elif delete_mode == DeleteMode.RANGE:
            # 删除从第{p_start}段第{c_start}个字符到第{p_end}段第{c_end}个字符之间的内容
            start_paragraph = doc.Paragraphs(p_start)
            end_paragraph = doc.Paragraphs(p_end)

            # 获取开始和结束位置的字符范围
            start_range = start_paragraph.Range.Characters(c_start + 1)
            end_range = end_paragraph.Range.Characters(c_end)

            # 选择从开始位置到结束位置的内容
            doc.Application.Selection.SetRange(Start=start_range.Start, End=end_range.End)

            # 删除选中的内容
            doc.Application.Selection.Delete()

    @classmethod
    def create_comment(
        cls,
        doc: object = None,
        paragraph_idx: int = 1,
        start: int = 1,
        end: int = 1,
        comment: str = "",
        comment_type: CommentType = CommentType.POSITION,
        target_str: str = "",
        comment_all: bool = True,
        comment_index: int = 1,
    ):
        doc.Activate()
        selection = doc.Application.Selection
        if comment_type == CommentType.POSITION:
            selection.SetRange(
                Start=doc.Paragraphs(paragraph_idx).Range.Start,
                End=doc.Paragraphs(paragraph_idx).Range.End,
            )
            label_text = doc.Paragraphs(paragraph_idx).Range.Text[start:end]
            selection.Find.Execute(label_text)
            doc.Comments.Add(Range=selection.Range, Text=comment)
        elif comment_type == CommentType.CONTENT:
            if not target_str:
                return
            if comment_all:
                selection.HomeKey(Unit=6, Extend=0)  # 将插入点移动到到文档首位
                while selection.Find.Execute(FindText=target_str, Forward=True, MatchCase=True):
                    doc.Comments.Add(Range=selection.Range, Text=comment)
            else:
                find_times = 0
                selection.HomeKey(Unit=6, Extend=0)  # 将插入点移动到到文档首位
                while selection.Find.Execute(FindText=target_str, Forward=True, MatchCase=True):
                    find_times += 1
                    if find_times == comment_index:
                        doc.Comments.Add(Range=selection.Range, Text=comment)
                        break

    @classmethod
    def delete_comment(cls, doc: object = None, comment_index: int = 1, delete_all: bool = False):
        doc.Activate()
        if delete_all is True:
            count = doc.Comments.Count
            for i in range(0, count):
                doc.Comments(count - i).DeleteRecursively()
        else:
            doc.Comments(comment_index).DeleteRecursively()

    @classmethod
    def convert_to_txt(
        cls,
        doc: object = None,
        output_path: str = "",
        output_name: str = "",
        save_type: SaveFileType = SaveFileType.WARN,
    ):
        filename = f"{output_name}.txt"
        if WordDocumentCore.check_file_in_path(output_path, filename):
            if save_type == SaveFileType.WARN:
                raise BaseException(FILENAME_ALREADY_EXISTS_ERROR.format(filename), "")
            if save_type == SaveFileType.GENERATE:
                # 生成非重复文件名
                counter = 1
                oldfilename, _ = os.path.splitext(filename)
                while WordDocumentCore.check_file_in_path(output_path, filename):
                    filename = f"{oldfilename}_{counter}.txt"
                    counter += 1

            elif save_type == SaveFileType.OVERWRITE:
                fullpath = os.path.join(output_path, filename)
                os.remove(fullpath)
        oldfilepath = doc.FullName
        pydoc = Document(oldfilepath)
        new_path = os.path.join(output_path, filename)
        with open(new_path, "w", encoding="utf-8") as txt_file:
            for para in pydoc.paragraphs:
                txt_file.write(para.text + "\n")

    @classmethod
    def convert_to_pdf(
        cls,
        doc: object = None,
        output_path: str = "",
        output_name: str = "新建PDF",
        page_type: ConvertPageType = ConvertPageType.ALL,
        page_start: int = 1,
        page_end: int = 1,
        save_type: SaveFileType = SaveFileType.WARN,
    ):
        filename = f"{output_name}.pdf"
        if WordDocumentCore.check_file_in_path(output_path, filename):
            if save_type == SaveFileType.WARN:
                raise BaseException(FILENAME_ALREADY_EXISTS_ERROR.format(filename), "")
            if save_type == SaveFileType.GENERATE:
                # 生成非重复文件名
                counter = 1
                oldfilename, _ = os.path.splitext(filename)
                while WordDocumentCore.check_file_in_path(output_path, filename):
                    filename = f"{oldfilename}_{counter}.pdf"
                    counter += 1

        new_path = os.path.join(output_path, filename)
        doc.ExportAsFixedFormat(
            OutputFileName=new_path,
            ExportFormat=17,
            OpenAfterExport=True,
            Range=page_type.value,
            From=page_start,
            To=page_end,
        )
