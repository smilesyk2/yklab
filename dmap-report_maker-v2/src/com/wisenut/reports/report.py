# -*- coding: utf-8 -*- 
import com.wisenut.dao.mariadbclient as db
import com.wisenut.dao.esclient as es
import com.wisenut.utils.file_util as file_util
import re, os
import math
from com.wisenut.enums.query import Query
from com.wisenut.enums.channel import Channel
from datetime import date, timedelta
from com.wisenut.config import Config
import logging
from com.wisenut import myLogger

class Report:
    seq = -1
    reg_dt = ""
    report_type = ""
    project_name = ""
    channel = ""
    start_date = ""
    end_date = ""
    dataset_names = ""
    compare = False
    queryObj = None
    
    file_name = ""
    file_path = ""
    
    #BASE_EXCEL_DIRECTORY='/data/dmap-data/dmap-excel'
    conf = Config()
    BASE_EXCEL_DIRECTORY=conf.get_report_home()
    
    DOCUMENTS_FIELDS        = [ 'doc_datetime','doc_writer','doc_url','doc_title','doc_content','depth1_nm','depth2_nm','depth3_nm']
    DOCUMENTS_FIELDS_KOREAN = [ '게시일','작성자','URL','제목','내용','채널1','채널2','채널3']
    #EMOTIONS_FIELDS         = [ 'conceptlevel1', 'conceptlevel2', 'conceptlevel3', 'emotion_type', 'matched_text.string', 'depth1_nm', 'depth2_nm', 'depth3_nm', 'doc_datetime', 'doc_writer', 'doc_url', 'doc_title']
    EMOTIONS_FIELDS         = [ 'conceptlevel1', 'conceptlevel2', 'conceptlevel3', 'emotion_type', 'matched_text.string']
    #EMOTIONS_FIELDS_KOREAN  = [ '대분류', '중분류', '소분류', '감성', '분석문장', '채널1', '채널2', '채널3', '게시일', '작성자', 'URL', '제목']
    EMOTIONS_FIELDS_KOREAN  = [ '대분류', '중분류', '소분류', '감성', '분석문장']
    
    HEADER_FORMAT = {
        'bold' : True,
        'font_size' : 9,
        'bg_color' : '#F2F2F2',
        'align' : 'center',
        'border' : 1
    }
    DEFAULT_FORMAT = {
        'font_size' : 9,
        'border' : 1
    }
    
    logger = myLogger.getMyLogger("report", False, True, logging.DEBUG)
    
    def __init__(self, params):
        self.compare = True if params['compare_yn']=='Y' else False
        self.start_date = re.sub("[-:\s]", "", params['start_date'])[:8]
        self.end_date = re.sub("[-:\s]", "", params['end_date'])[:8]
        self.seq = params['seq']
        self.reg_dt = re.sub("[-:\s]", "", params['reg_dt'])
        self.report_type = db.get_exceltype_name(params['type_cd']) # RSP -> 리포트_소셜모니터링_추이분석
        self.project_name = db.get_project_name(params['project_seq'])
        self.channel = '전체' if not params['channels'] or params['channels']=='all' else "채널일부"
        
        self.dataset_names = ",".join([db.get_dataset_name(x) if db.get_dataset_name(x)!=None else 'unknown' for x in params['datasets'].split("^")]) if params['datasets'] else '' # 6^7^15 -> 신라면,안성탕면,짜파게티
        if os.name == 'nt' and bool(re.match("[\/\\\"*?<>\|]", self.dataset_names)):
            self.dataset_names = re.sub("[\/\\\"*?<>\|]", "_", self.dataset_names)
            
        self.queryObj = Query()
        
        compare_yn = "동일기간비교" if params['compare_yn']=='Y' else "해당기간"
        
        if not params['datasets']: # 검색트렌드
            self.file_name = "_".join([str(self.seq), self.report_type, self.start_date, self.end_date, compare_yn]) + ".xlsx"
        else: # 소셜모니터링
            if len(params['datasets'].split("^"))>1:
                self.file_name = "_".join([str(self.seq), self.report_type, self.channel, self.start_date, self.end_date, compare_yn]) + ".xlsx"
            else:
                self.file_name = "_".join([str(self.seq), self.report_type+"("+self.dataset_names+")", self.channel, self.start_date, self.end_date, compare_yn]) + ".xlsx"
                
        self.logger.info("=======================================================================================")
        for k, v in params.items():
            self.logger.info(k + " :\t\t" + str(v))
        self.logger.info("=======================================================================================")    
        
    def get_file_name(self):
        return self.file_name
    
    def create_file_path(self):
        self.file_path = os.path.join(self.BASE_EXCEL_DIRECTORY, self.reg_dt)
        return file_util.search_create_directory( self.file_path )
    
    # 표지   
    def cover_page(self, params):
        worksheet = self.workbook.add_worksheet('표지')
        worksheet.write(0, 0, '프로젝트명', self.header)
        worksheet.write(1, 0, '분석메뉴', self.header)
        worksheet.write(2, 0, '데이터셋', self.header)
        worksheet.write(3, 0, '기간(당기)', self.header)
        worksheet.write(4, 0, '채널', self.header)
        worksheet.write(0, 1, self.project_name, self.default)
        worksheet.write(1, 1, self.report_type, self.default)
        worksheet.write(2, 1, self.dataset_names, self.default)
        if params['compare_yn']=='Y':
            arr_date = []
            # 기준날짜
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            
            for i in range(4):
                time_interval = end_date-start_date
                # 비교 날짜들(1time_interval before)
                this_end_date = end_date - (time_interval+timedelta(days=1))*i # 곱해진 간격만큼 이전 날짜를 구함
                
                arr_date.append("%s ~ %s"%((this_end_date-time_interval).strftime('%Y.%m.%d(%a)'), this_end_date.strftime('%Y.%m.%d(%a)')))
                
            worksheet.write(3, 1, ", ".join(arr_date), self.default)
        else:
            worksheet.write(3, 1, "~".join([self.start_date, self.end_date]), self.default)
            
        if self.channel=="채널일부" and len(params['channels'].split(";"))>1:
            self.channel += "("
            for c in params['channels'].split(";"):
                channel_info = db.get_channel_name(Channel.DEPTH1.value, c.split("^")[0])
                if channel_info:
                    self.channel += channel_info[0] + ","
                    
            self.channel = re.sub(",$", "", self.channel)
            self.channel += ")"
                
        worksheet.write(4, 1, self.channel, self.default)
    
                        
