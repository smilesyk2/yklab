# -*- coding : utf-8 -*-
'''
Created on 2017. 6. 20.

@author: Holly
*** params에 변경이 생겨서 원본이 훼손될 가능성이 있다면, parameter에 params 값을 넘길 때, copy.copy(params)로 넘겨준다.(call-by-value 방식)
'''
import xlsxwriter
import os
import com.wisenut.dao.esclient as es
from com.wisenut.reports.report import Report
import copy
from datetime import timedelta, date
from com.wisenut.enums.query import Query
import math

class RST(Report):
    workbook = None
    header = None
    default = None
    
    # 일자별 수집량
    
                    
                    
    def doc_list_per_topic(self, params):
        size = 10000 # 페이징 사이즈
        
        # 검색 시작
        result = es.get_documents_per_topic(params, size, "가뭄")
        _scroll_id = result["_scroll_id"] if "_scroll_id" in result else None
        totalcount = result["hits"]["total"] if "hits" in result and result["hits"]["total"] else 0
        
        if totalcount > 0:
            print("[doc_list_per_topic] totalcount >>> "+ str(totalcount))
            worksheet = self.workbook.add_worksheet("원문(%s)"%"~".join([params['start_date'][0:10],params['end_date'][0:10]]))
            
            # 엑셀 헤더
            for colidx, field in enumerate(self.CRAWL_DOC_FIELDS):
                worksheet.write(0, colidx, field, self.header)
            
            for rownum, row in enumerate(result["hits"]["hits"]):
                for col, field in enumerate(self.CRAWL_DOC_FIELDS):
                    worksheet.write(rownum+1, col, row["_source"][field], self.default)
                    
                    
            # 결과건수가 한 페이지 사이즈보다 큰 경우, scroll을 이용해서 paging하며 결과를 가져옴.
            # 용량이 클 것으로 예상하여 엑셀 파일도 새로 생성.            
            if totalcount > size:
                for page in range(1, math.ceil(totalcount/size)): # 0, 1, 2, ....
                    worksheet = self.workbook.add_worksheet("원문(%s)(%d)"%("~".join([params['start_date'][0:10],params['end_date'][0:10]]), page))#>%s(%d)"%(this_dataset_name,page))
                    
                    # 엑셀 헤더
                    for colidx, field in enumerate(self.CRAWL_DOC_FIELDS):
                        worksheet.write(0, colidx, field, self.header)
                        
                    result2 = es.get_documents_per_topic(params, size, "가뭄", scroll_id=_scroll_id)
                    _scroll_id = result2["_scroll_id"] if "_scroll_id" in result2 else None
                    # 다시 요청했을 때 결과가 있을 때만 수행
                    if 'hits' in result2:
                        for rownum2, row2 in enumerate(result2["hits"]["hits"]):
                            for col, field in enumerate(self.CRAWL_DOC_FIELDS):
                                worksheet.write(rownum2+1, col, row2["_source"][field], self.default)
                            
                            
        
    def create_report(self, params):
        print("[rst_maker>create_report] start!")
        self.workbook = xlsxwriter.Workbook(os.path.join(self.file_path, self.file_name+".xlsx"), options={'strings_to_urls': False} )
        print(os.path.join(self.file_path, self.file_name+".xlsx"))
        self.header = self.workbook.add_format(self.HEADER_FORMAT)
        self.default = self.workbook.add_format(self.DEFAULT_FORMAT)
        
        self.cover_page(copy.copy(params))
        if params['compare_yn']=='N':
            self.topics_list(copy.copy(params))
        else:
            # 기준날짜
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            
            for i in range(4):
                time_interval = end_date-start_date
                # 비교 날짜들(1time_interval before)
                this_end_date = end_date - (time_interval+timedelta(days=1))*i # 곱해진 간격만큼 이전 날짜를 구함
                
                new_params = copy.copy(params) 
                new_params['start_date'] = (this_end_date-time_interval).strftime('%Y-%m-%dT00:00:00') 
                new_params['end_date'] = this_end_date.strftime('%Y-%m-%dT23:59:59')
                
                self.topics_list(new_params)
        self.doc_list_per_topic(params)
            
        self.workbook.close()