# -*- coding: utf-8 -*- 
'''
Created on 2017. 6. 20.

@author: Holly
*** params에 변경이 생겨서 원본이 훼손될 가능성이 있다면, parameter에 params 값을 넘길 때, copy.copy(params)로 넘겨준다.(call-by-value 방식)
'''
import xlsxwriter
import os
import com.wisenut.dao.esclient as es
import com.wisenut.dao.mariadbclient as mariadb
from com.wisenut.reports.report import Report 
import copy
from datetime import timedelta, date
import re
import math
from com.wisenut.enums.query import Query

class ReportEmotions(Report):
    workbook = None
    header = None
    default = None
    INDEX_NAME="documents-*"
    
    # 데이터셋이 여러개일 때 데이터셋별로 emotions(index)의 추이
    def dataset_count_per_day_in_emotions(self, params):
        worksheet = self.workbook.add_worksheet('분석량 추이')
        sum_per_dataset = {}
        
        # 헤더
        worksheet.write(0, 0, '일자', self.header)
        col_header = 0
        for dataset_name in self.dataset_names.split(","):
            worksheet.write(0, 1+col_header, dataset_name, self.header)
            col_header += 1
        worksheet.write(0, 1+col_header, '합계', self.header)
            
        # 데이터
        qdsl = self.queryObj.DATASET_COUNT_PER_DAY_IN_EMOTIONS(self.compare)
        self.logger.debug("[ReportEmotions][dataset_count_per_day_in_emotions] %s" % qdsl)
        
        result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
        
        if 'hits' in result and result['hits']['total']>0:
            row = 0
            for bucket in result['aggregations']['my_aggs1']['buckets']:
                if self.compare:
                    worksheet.write(1+row, 0, bucket['key'], self.header)
                else:
                    worksheet.write(1+row, 0, bucket['key_as_string'], self.header)
                    
                sum_per_day = 0
                col_body = 0
                for dataset_seq in params['datasets'].split("^"):
                    count_of_this_dataset = bucket['my_aggs2']['my_aggs3']['buckets'][dataset_seq]['doc_count']
                    sum_per_day += count_of_this_dataset
                    sum_per_dataset[dataset_seq] = count_of_this_dataset if dataset_seq not in sum_per_dataset else sum_per_dataset[dataset_seq]+count_of_this_dataset
                    
                    worksheet.write(1+row, 1+col_body, count_of_this_dataset, self.default)
                    col_body += 1
                    
                worksheet.write(1+row, 1+col_body, sum_per_day, self.default)
                row += 1
                
            # 합계
            if len(params['datasets'].split("^"))==1:
                worksheet.write(1+row, 0, '합계', self.header)
                col_footer = 0
                for dataset_seq in params['datasets'].split("^"):
                    worksheet.write(1+row, 1+col_footer, '', self.header)
                    col_footer += 1
                worksheet.write(1+row, 1+col_footer, sum_per_dataset[dataset_seq], self.header)
                
                
                
    def dataset_occupations_per_depth1_in_emotions(self, params):
        worksheet = self.workbook.add_worksheet('채널분석량')
        # 헤더
        if not self.compare:
            worksheet.write(0, 0, '데이터셋', self.header)
            worksheet.write(0, 1, '채널', self.header)
            worksheet.write(0, 2, '분석량', self.header)
        else:
            worksheet.write(0, 0, '날짜범위', self.header)
            worksheet.write(0, 1, '데이터셋', self.header)
            worksheet.write(0, 2, '채널', self.header)
            worksheet.write(0, 3, '분석량', self.header)
            
        # 데이터
        qdsl = self.queryObj.DATASET_OCCUPATIONS_PER_DEPTH1_IN_EMOTIONS(self.compare)
        self.logger.debug("[ReportEmotions][dataset_occupations_per_depth1_in_emotions] %s" % qdsl)
        
        result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
        #total = result['hits']['total']
        total = 0
        row = 0
        
        if 'hits' in result and result['hits']['total']>0:
            if not self.compare:
                for dataset_seq in params['datasets'].split("^"):
                    for bucket2 in result['aggregations']['my_aggs1']['buckets'][dataset_seq]['my_aggs2']['buckets']:
                        dataset_name = mariadb.get_dataset_name(dataset_seq) if mariadb.get_dataset_name(dataset_seq)!=None else 'unknown'
                        
                        worksheet.write(1+row, 0, dataset_name, self.default) # 데이터셋 이름
                        worksheet.write(1+row, 1, bucket2['key'], self.default) # 데이터셋 이름
                        worksheet.write(1+row, 2, bucket2['my_aggs3']['doc_count'], self.default) # 데이터셋 이름
                        
                        total += bucket2['my_aggs3']['doc_count']
                        row += 1
            else:
                for bucket1 in result['aggregations']['my_aggs1']['buckets']:
                    for dataset_seq in params['datasets'].split("^"):
                        for bucket3 in bucket1['my_aggs2']['buckets'][dataset_seq]['my_aggs3']['buckets']:
                            dataset_name = mariadb.get_dataset_name(dataset_seq) if mariadb.get_dataset_name(dataset_seq) is not None else 'unknown'
                            
                            worksheet.write(1+row, 0, bucket1['key'], self.default) # 날짜범위
                            worksheet.write(1+row, 1, dataset_name, self.default) # 데이터셋 이름
                            worksheet.write(1+row, 2, bucket3['key'], self.default) # 채널명
                            worksheet.write(1+row, 3, bucket3['my_aggs4']['doc_count'], self.default) # 분석량
                            
                            total += bucket3['my_aggs4']['doc_count']
                            row += 1
                         
            if len(params['datasets'].split("^"))==1:   
                worksheet.write(row+1, 0, '합계', self.header)
                worksheet.write(row+1, 1, '', self.header)
                if not self.compare:
                    worksheet.write(row+1, 2, total, self.header)
                else:
                    worksheet.write(row+1, 2, '', self.header)
                    worksheet.write(row+1, 3, total, self.header)
                
                
        
        
    def dataset_occupations_per_depth3_in_emotions(self, params):
        worksheet = self.workbook.add_worksheet('채널분석량 상세')
        #arr_dataset_names = self.dataset_names.split(",")
        # 헤더
        worksheet.write(0, 0, '데이터셋', self.header)
        worksheet.write(0, 1, '1Depth', self.header)
        worksheet.write(0, 2, '2Depth', self.header)
        worksheet.write(0, 3, '3Depth', self.header)
        worksheet.write(0, 4, '분석량', self.header)
            
        # 데이터
        qdsl = self.queryObj.DATASET_OCCUPATIONS_PER_DEPTH3_IN_EMOTIONS(self.compare)
        self.logger.debug("[ReportEmotions][dataset_occupations_per_depth3_in_emotions] %s" % qdsl)
        
        result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
        total = 0
        row = 0
        
        if 'hits' in result and result['hits']['total']>0:
            for dataset_seq in params['datasets'].split("^"):
                for bucket2 in result['aggregations']['my_aggs1']['buckets'][dataset_seq]['my_aggs2']['buckets']:
                    dataset_name = mariadb.get_dataset_name(dataset_seq) if mariadb.get_dataset_name(dataset_seq)!=None else 'unknown'
                    
                    if len(bucket2['key'].split(">"))>2:
                        depth1, depth2, depth3 = bucket2['key'].split(">")
                    else:
                        depth1, depth2 = bucket2['key'].split(">")
                        depth3 = ''
                        
                    worksheet.write(1+row, 0, dataset_name, self.default) # 데이터셋 이름
                    worksheet.write(1+row, 1, re.sub("[\[\]]", "", depth1), self.default) # 데이터셋 이름
                    worksheet.write(1+row, 2, re.sub("[\[\]]", "", depth2), self.default) # 데이터셋 이름
                    worksheet.write(1+row, 3, re.sub("[\[\]]", "", depth3), self.default) # 데이터셋 이름
                    worksheet.write(1+row, 4, bucket2['my_aggs3']['doc_count'], self.default) # 데이터셋 이름
                    
                    total += bucket2['my_aggs3']['doc_count']
                    row += 1
                    
            if len(params['datasets'].split("^"))==1:      
                worksheet.write(row+1, 0, '합계', self.header)
                worksheet.write(row+1, 1, '', self.header)
                worksheet.write(row+1, 2, '', self.header)
                worksheet.write(row+1, 3, '', self.header)
                worksheet.write(row+1, 4, total, self.header)
            
    # 긍부정 분석        
    def occupation_per_emotions(self, params):
        worksheet = self.workbook.add_worksheet('감성분석 점유율')
        #arr_dataset_names = self.dataset_names.split(",")
        
        if not self.compare:
            # 헤더
            worksheet.write(0, 0, '긍부정', self.header)
            worksheet.write(0, 1, '분석량', self.header)
            worksheet.write(0, 2, '점유율(%)', self.header)
            
            # 데이터
            qdsl = self.queryObj.EMOTIONS_OCCUPATIONS(self.compare)
            self.logger.debug("[ReportEmotions][occupation_per_emotions] %s" % qdsl)
            
            result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
            total = result['hits']['total']
            total_percentage = 0.0
            row = 0
            for bucket in result['aggregations']['my_aggs1']['my_aggs2']['buckets']:
                worksheet.write(1+row, 0, bucket['key'], self.default) # 데이터셋 이름
                worksheet.write(1+row, 1, bucket['doc_count'], self.default) # 데이터셋 이름
                
                total_percentage += bucket['doc_count']/total*100
                worksheet.write(1+row, 2, bucket['doc_count']/total*100, self.default) # 데이터셋 이름
                row += 1
            
            worksheet.write(row+1, 0, '합계', self.header)
            worksheet.write(row+1, 1, total, self.header)
            worksheet.write(row+1, 2, total_percentage, self.header)
        else:
            # 헤더
            worksheet.write(0, 0, '날짜', self.header)
            worksheet.write(0, 1, '긍부정', self.header)
            worksheet.write(0, 2, '분석량', self.header)
            
            # 데이터
            qdsl = self.queryObj.EMOTIONS_OCCUPATIONS(self.compare)
            self.logger.debug("[ReportEmotions][occupation_per_emotions] %s" % qdsl)
            
            result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
            total = result['hits']['total']
            total_percentage = 0.0
            row = 0
            for bucket1 in result['aggregations']['my_aggs1']['buckets']:
                for bucket3 in bucket1['my_aggs2']['my_aggs3']['buckets']:
                    worksheet.write(1+row, 0, bucket1['key'], self.default) # 데이터셋 이름
                    worksheet.write(1+row, 1, bucket3['key'], self.default) # 데이터셋 이름
                    worksheet.write(1+row, 2, bucket3['doc_count'], self.default) # 데이터셋 이름
                    row += 1
            
            # 합계
            if len(params['datasets'].split("^"))==1:
                worksheet.write(row+1, 0, '합계', self.header)
                worksheet.write(row+1, 1, '', self.header)
                worksheet.write(row+1, 2, total, self.header)
                
                
                
                
    def emotions_per_day(self, params): 
        worksheet = self.workbook.add_worksheet('감성분석 추이')
        #arr_dataset_names = self.dataset_names.split(",")
        # 헤더
        worksheet.write(0, 0, '일자', self.header)
        worksheet.write(0, 1, '긍부정', self.header)
        worksheet.write(0, 2, '분석량', self.header)
        
        # 데이터
        qdsl = self.queryObj.EMOTIONS_PROGRESS()
        self.logger.debug("[ReportEmotions][emotions_per_day] %s" % qdsl)
        
        result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
        #total = result['hits']['total']
        total = 0
        row = 0
        for bucket1 in result['aggregations']['my_aggs1']['buckets']:
            for bucket3 in bucket1['my_aggs2']['my_aggs3']['buckets']:
                worksheet.write(1+row, 0, bucket1['key_as_string'], self.default)
                worksheet.write(1+row, 1, bucket3['key'], self.default)
                worksheet.write(1+row, 2, bucket3['doc_count'], self.default)
                
                total += bucket3['doc_count']
                row += 1
        
        # 합꼐
        if len(params['datasets'].split("^"))==1:
            worksheet.write(row+1, 0, '합계', self.header)
            worksheet.write(row+1, 1, '', self.header)
            worksheet.write(row+1, 2, total, self.header)
        
        
        
        
    # 채널별 수집량        
    def emotions_per_channel(self, params):
        worksheet = self.workbook.add_worksheet('채널별 감성분석')
        #arr_dataset_names = self.dataset_names.split(",")
        
        # 데이터
        qdsl = self.queryObj.EMOTIONS_PER_DEPTH1(self.compare)
        self.logger.debug("[ReportEmotions][emotions_per_channel] %s" % qdsl)
        
        result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
        #total = result['hits']['total']
        total = 0
        row = 0
        
        # 헤더
        if not self.compare:
            worksheet.write(0, 0, '채널', self.header)
            worksheet.write(0, 1, '긍부정', self.header)
            worksheet.write(0, 2, '분석량', self.header)
            
            for bucket1 in result['aggregations']['my_aggs1']['buckets']:
                for bucket2 in bucket1['my_aggs2']['my_aggs3']['buckets']:
                    worksheet.write(1+row, 0, mariadb.get_channel_name(1, bucket1['key'])[0], self.default)
                    worksheet.write(1+row, 1, bucket2['key'], self.default)
                    worksheet.write(1+row, 2, bucket2['doc_count'], self.default)
                    
                    total += bucket2['doc_count']
                    row += 1
                    
            # 합꼐
            if len(params['datasets'].split("^"))==1:
                worksheet.write(row+1, 0, '합계', self.header)
                worksheet.write(row+1, 1, '', self.header)
                worksheet.write(row+1, 2, total, self.header)
        else:
            worksheet.write(0, 0, '일자', self.header)
            worksheet.write(0, 1, '채널', self.header)
            worksheet.write(0, 2, '긍부정', self.header)
            worksheet.write(0, 3, '분석량', self.header)
            
            for bucket1 in result['aggregations']['my_aggs1']['buckets']:
                for bucket2 in bucket1['my_aggs2']['buckets']:
                    for bucket4 in bucket2['my_aggs3']['my_aggs4']['buckets']:
                        worksheet.write(1+row, 0, bucket1['key'], self.default)
                        worksheet.write(1+row, 1, mariadb.get_channel_name(1, bucket2['key'])[0], self.default)
                        worksheet.write(1+row, 2, bucket4['key'], self.default)
                        worksheet.write(1+row, 3, bucket4['doc_count'], self.default)
                        
                        total += bucket4['doc_count']
                        row += 1
        
            # 합꼐
            if len(params['datasets'].split("^"))==1:
                worksheet.write(row+1, 0, '합계', self.header)
                worksheet.write(row+1, 1, '', self.header)
                worksheet.write(row+1, 2, '', self.header)
                worksheet.write(row+1, 3, total, self.header)
    
    
    
    
    
    def emotions_per_causes(self, params):
        worksheet = self.workbook.add_worksheet('언급원인별 분석')
        #arr_dataset_names = self.dataset_names.split(",")
        if not self.compare:
            # 헤더
            worksheet.write(0, 0, '1Depth', self.header)
            worksheet.write(0, 1, '2Depth', self.header)
            worksheet.write(0, 2, '3Depth', self.header)
            worksheet.write(0, 3, '대분류', self.header)
            worksheet.write(0, 4, '중분류', self.header)
            worksheet.write(0, 5, '소분류', self.header)
            worksheet.write(0, 6, '긍부정', self.header)
            worksheet.write(0, 7, '분석량', self.header)
                
            # 데이터
            qdsl = self.queryObj.EMOTIONS_PER_CAUSES(self.compare)
            self.logger.debug("[ReportEmotions][emotions_per_causes] %s" % qdsl)
            
            result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
            #total = result['hits']['total']
            total = 0
            row = 0
            for bucket1 in result['aggregations']['my_aggs1']['buckets']:
                for bucket3 in bucket1['my_aggs2']['my_aggs3']['buckets']:
                    for bucket4 in bucket3['my_aggs4']['buckets']:
                        for bucket5 in bucket4['my_aggs5']['buckets']:
                            for bucket6 in bucket5['my_aggs6']['buckets']:
                                depth_level = bucket1['key'].split(">")
                                worksheet.write(1+row, 0, re.sub("[\[\]]", "", depth_level[0]) if len(bucket1['key'].split(">"))>=0 else '', self.default)
                                worksheet.write(1+row, 1, re.sub("[\[\]]", "", depth_level[1]) if len(bucket1['key'].split(">"))>=1 else '', self.default)
                                worksheet.write(1+row, 2, re.sub("[\[\]]", "", depth_level[2]) if len(bucket1['key'].split(">"))>=2 else '', self.default)
                                worksheet.write(1+row, 3, bucket3['key'], self.default)
                                worksheet.write(1+row, 4, bucket4['key'], self.default)
                                worksheet.write(1+row, 5, bucket5['key'], self.default)
                                worksheet.write(1+row, 6, bucket6['key'], self.default)
                                worksheet.write(1+row, 7, bucket6['doc_count'], self.default)
                                
                                total += bucket6['doc_count']
                                row += 1
                                
            # 합꼐
            if len(params['datasets'].split("^"))==1:
                worksheet.write(row+1, 0, '합계', self.header)
                worksheet.write(row+1, 1, '', self.header)
                worksheet.write(row+1, 2, '', self.header)
                worksheet.write(row+1, 3, '', self.header)
                worksheet.write(row+1, 4, '', self.header)
                worksheet.write(row+1, 5, '', self.header)
                worksheet.write(row+1, 6, '', self.header)
                worksheet.write(row+1, 7, total, self.header)
        else:
            # 헤더
            worksheet.write(0, 0, '날짜', self.header)
            worksheet.write(0, 1, '1Depth', self.header)
            worksheet.write(0, 2, '2Depth', self.header)
            worksheet.write(0, 3, '3Depth', self.header)
            worksheet.write(0, 4, '대분류', self.header)
            worksheet.write(0, 5, '중분류', self.header)
            worksheet.write(0, 6, '소분류', self.header)
            worksheet.write(0, 7, '긍부정', self.header)
            worksheet.write(0, 8, '분석량', self.header)
                
            # 데이터
            qdsl = self.queryObj.EMOTIONS_PER_CAUSES(self.compare)
            self.logger.debug("[ReportEmotions][emotions_per_causes] %s" % qdsl)
            
            result = es.get_aggregations(copy.copy(qdsl), params, self.INDEX_NAME)
            #total = result['hits']['total']
            total = 0 
            row = 0
            for bucket1 in result['aggregations']['my_aggs1']['buckets']:
                for bucket3 in bucket1['my_aggs2']['buckets']:
                    for bucket4 in bucket3['my_aggs3']['my_aggs4']['buckets']:
                        for bucket5 in bucket4['my_aggs5']['buckets']:
                            for bucket6 in bucket5['my_aggs6']['buckets']:
                                for bucket7 in bucket6['my_aggs7']['buckets']:
                                    # 날짜 범위
                                    worksheet.write(1+row, 0, bucket1['key'], self.default)
                                    
                                    # 1Depth, 2Depth, 3Depth
                                    depth_level = bucket3['key'].split(">")
                                    worksheet.write(1+row, 1, re.sub("[\[\]]", "", depth_level[0]) if len(bucket3['key'].split(">"))>=0 else '', self.default)
                                    worksheet.write(1+row, 2, re.sub("[\[\]]", "", depth_level[1]) if len(bucket3['key'].split(">"))>=1 else '', self.default)
                                    worksheet.write(1+row, 3, re.sub("[\[\]]", "", depth_level[2]) if len(bucket3['key'].split(">"))>=2 else '', self.default)
                                    
                                    worksheet.write(1+row, 4, bucket4['key'], self.default) # 대분류
                                    worksheet.write(1+row, 5, bucket5['key'], self.default) # 중분류
                                    worksheet.write(1+row, 6, bucket6['key'], self.default) # 소분류
                                    worksheet.write(1+row, 7, bucket7['key'], self.default) # 긍부정
                                    worksheet.write(1+row, 8, bucket7['doc_count'], self.default)
                                    
                                    total += bucket7['doc_count']
                                    row += 1
                                
            # 합꼐
            if len(params['datasets'].split("^"))==1:
                worksheet.write(row+1, 0, '합계', self.header)
                worksheet.write(row+1, 1, '', self.header)
                worksheet.write(row+1, 2, '', self.header)
                worksheet.write(row+1, 3, '', self.header)
                worksheet.write(row+1, 4, '', self.header)
                worksheet.write(row+1, 5, '', self.header)
                worksheet.write(row+1, 6, '', self.header)
                worksheet.write(row+1, 7, '', self.header)
                worksheet.write(row+1, 8, total, self.header)
      
    
    
    # 원문
    def create_emotions_list(self, params):
        thisQueryObj = Query(params)
        
        size = 10000 # 페이징 사이즈
        
        # 검색 시작
        #result = es.get_documents(params, size, index, "")
        self.logger.debug("[ReportEmotions][create_emotions_list] %s" % thisQueryObj.get_emotions_query())
        
        totalCount = es.get_count(self.INDEX_NAME+"/doc/_count", thisQueryObj.get_emotions_query())
        
        #if "hits" in result and result["hits"]["total"] > 0:
        if totalCount > 0 :
            scroll_id = None
                    
            # 결과건수가 한 페이지 사이즈보다 큰 경우, scroll을 이용해서 paging하며 결과를 가져옴.
            # 용량이 클 것으로 예상하여 엑셀 파일도 새로 생성.            
            #if "hits" in result and result["hits"]["total"] > size:
            for page in range(math.ceil(totalCount/size)): # 0, 1, 2, ....
                scrolled_result = es.get_list(self.INDEX_NAME+"/doc/_search", thisQueryObj.get_emotions_query(), size, scroll_id)
                worksheet = self.workbook.add_worksheet("원문(%s)(%d)"%("~".join([params['start_date'][0:10],params['end_date'][0:10]]), page+1))#>%s(%d)"%(this_dataset_name,page))
                scroll_id = copy.copy(scrolled_result['_scroll_id'])
                
                # 엑셀 헤더
                for colidx, field in enumerate(self.EMOTIONS_FIELDS_KOREAN):
                    worksheet.write(0, colidx, field, self.header)
                    
                for row, this_result in enumerate(scrolled_result["hits"]["hits"]):
                    for col, field in enumerate(self.EMOTIONS_FIELDS):
                        if "." in field:
                            field, subfield = field.split(".")
                            
                            val = this_result["_source"][field][subfield] if field in this_result["_source"] and subfield in this_result["_source"][field] else "null"
                            worksheet.write(row+1, col, val, self.default)
                        else:
                            val = this_result["_source"][field] if field in this_result["_source"] else "null"
                            worksheet.write(row+1, col, val, self.default)
                    
                if page == math.ceil(totalCount/size)-1 and scroll_id is not None: # 마지막 페이지를 처리하고 나면 scroll을 clear
                    es.clear_scroll(scroll_id)
                    scroll_id = None
    
    
    
    
    def create_report(self, params):
        self.workbook = xlsxwriter.Workbook(os.path.join(self.BASE_EXCEL_DIRECTORY, self.file_path.replace("/", os.path.sep), self.file_name), options={'strings_to_urls': False, 'strings_to_numbers': True} )
        self.header = self.workbook.add_format(self.HEADER_FORMAT)
        self.default = self.workbook.add_format(self.DEFAULT_FORMAT)
        
        self.cover_page(copy.copy(params))
        if 'datasets' in params and len(params['datasets'].split("^"))>1:
            self.dataset_count_per_day_in_emotions(copy.copy(params)) # 이게 새로운거
            self.dataset_occupations_per_depth1_in_emotions(copy.copy(params)) # 이게 새로운거(copy.copy(params))
            if not self.compare:
                self.dataset_occupations_per_depth3_in_emotions(copy.copy(params)) # 이게 새로운거(copy.copy(params))
        else:
            self.occupation_per_emotions(copy.copy(params))
            if not self.compare:
                self.emotions_per_day(copy.copy(params))
            self.emotions_per_channel(copy.copy(params))
            self.emotions_per_causes(copy.copy(params))
               
            # 2017.07.24 데이터셋이 한 개일 때만 원문 리스트를 만든다.
            if not self.compare:
                self.create_emotions_list(copy.copy(params))
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
                    
                    self.create_emotions_list(new_params)
        
        
        self.workbook.close()