# -*- coding : utf-8 -*-
'''
Created on 2017. 6. 20.

@author: Holly
*** params에 변경이 생겨서 원본이 훼손될 가능성이 있다면, parameter에 params 값을 넘길 때, copy.copy(params)로 넘겨준다.(call-by-value 방식)
'''
import xlsxwriter
import os
from com.wisenut.dao import esclient as es
from com.wisenut.dao import mariadbclient as mariadb
from com.wisenut.reports.report import Report
from com.wisenut.enums.channel import Channel
import copy
from datetime import timedelta, date
from com.wisenut.enums.query import Query

class RSO(Report):
    workbook = None
    header = None
    default = None
    
    def occupation_per_dataset(self, params):
        worksheet = self.workbook.add_worksheet('데이터셋별 문서점유율')
        
        # 헤더
        worksheet.write(0, 0, '데이터셋', self.header)
        worksheet.write(0, 1, '문서수', self.header)
        worksheet.write(0, 2, '비율(%)', self.header)
        
        # 데이터
        result = es.get_statistics(Query.SOCIAL_OCCUPATION_DATASET, params['compare_yn'], params)
        rownum=1
        total_doc_count = result['hits']['total']
        for dataset_seq in params['datasets'].split("^"):
            dataset_doc_count = result['aggregations']['my_analysis']['buckets'][dataset_seq]['doc_count']
            worksheet.write(rownum, 0, mariadb.get_dataset_name(dataset_seq), self.default) # Depth1
            worksheet.write(rownum, 1, dataset_doc_count, self.default)
            worksheet.write(rownum, 2, round(dataset_doc_count/total_doc_count*100, 1), self.default)
            rownum+=1
        worksheet.write(rownum, 0, "합계", self.header)
        worksheet.write(rownum, 1, total_doc_count, self.header)
        worksheet.write(rownum, 2, "100", self.header)
        
        
    
                        
    # 포털 문서량
    def occupation_per_depth2(self, params, query_cd):
        sheet_name = ''
        if query_cd is Query.SOCIAL_OCCUPATION_COMMUNITY:
            sheet_name = '커뮤니티별 문서점유율'
        elif query_cd is Query.SOCIAL_OCCUPATION_MEDIA:
            sheet_name = '미디어별 문서점유율'
        elif query_cd is Query.SOCIAL_OCCUPATION_SNS:
            sheet_name = 'SNS별 문서점유율'
        elif query_cd is Query.SOCIAL_OCCUPATION_PORTAL:
            sheet_name = '포털별 문서점유율'
        elif query_cd is Query.SOCIAL_OCCUPATION_CLUB:
            sheet_name = '동호회별 문서점유율'
            
        print("sheet_name :: %s" % sheet_name)
        worksheet = self.workbook.add_worksheet(sheet_name)
            
        # 헤더
        worksheet.write(0, 0, '채널별', self.header)
        worksheet.write(0, 1, '문서수', self.header)
        worksheet.write(0, 2, '비율(%)', self.header)
        
        # 데이터
        result = es.get_statistics(query_cd, params['compare_yn'], params)
        rownum=1
        total_doc_count = result['hits']['total']
        for bucket in result['aggregations']['my_analysis']['buckets']:
            worksheet.write(rownum, 0, mariadb.get_channel_name(Channel.DEPTH2.value, int(bucket['key']))[Channel.DEPTH2.value-1], self.default) # Depth1
            worksheet.write(rownum, 1, bucket['doc_count'], self.default)
            worksheet.write(rownum, 2, round(bucket['doc_count']/total_doc_count*100, 1), self.default)
            rownum+=1
        worksheet.write(rownum, 0, "합계", self.header)
        worksheet.write(rownum, 1, total_doc_count, self.header)
        worksheet.write(rownum, 2, "100", self.header)

    
    
    # 긍부정 분석        
    def occupation_per_causes(self, params):
        worksheet = self.workbook.add_worksheet('요인별 문서 점유율')
        # 헤더
        worksheet.write(0, 0, '감정', self.header)
        for col, dataset_name in enumerate(self.dataset_names.split(",")):
            worksheet.write(0, 1+col, dataset_name, self.header)
            
        # 데이터
        result = es.get_statistics(Query.SOCIAL_OCCUPATION_CAUSES, params['compare_yn'], params)
        for row, bucket in enumerate(result['aggregations']['my_analysis']['buckets']):
            worksheet.write(1+row, 0, bucket['key'], self.header) # 긍정/부정/중립
            for col, dataset_seq in enumerate(params['datasets'].split("^")):
                worksheet.write(1+row, 1+col, bucket['my_datasets']['buckets'][dataset_seq]['doc_count'], self.default)
                
    
    def create_report(self, params):
        self.workbook = xlsxwriter.Workbook(os.path.join(self.file_path, self.file_name+".xlsx"), options={'strings_to_urls': False} )
        self.header = self.workbook.add_format(self.HEADER_FORMAT)
        self.default = self.workbook.add_format(self.DEFAULT_FORMAT)
        
        self.cover_page(copy.copy(params))
        self.occupation_per_dataset(copy.copy(params))
        self.occupation_per_channel(copy.copy(params))
        self.occupation_per_depth2(copy.copy(params), Query.SOCIAL_OCCUPATION_PORTAL)
        self.occupation_per_depth2(copy.copy(params), Query.SOCIAL_OCCUPATION_MEDIA)
        self.occupation_per_depth2(copy.copy(params), Query.SOCIAL_OCCUPATION_COMMUNITY)
        self.occupation_per_depth2(copy.copy(params), Query.SOCIAL_OCCUPATION_SNS)
        self.occupation_per_depth2(copy.copy(params), Query.SOCIAL_OCCUPATION_CLUB)
        # self.occupation_per_emotions(params) # 데이터 넣고 수정해야함.
        # self.occupation_per_causes(params) # 데이터 넣고 수정해야함.
        self.count_per_channel(copy.copy(params))
        
        if params['compare_yn']=='N':
            self.create_documents_list(copy.copy(params))
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
                
                self.create_documents_list(new_params)
        
        self.workbook.close()