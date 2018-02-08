# -*- coding: utf-8 -*- 
'''
Created on 2017. 6. 30.

@author: Holly
'''
from com.wisenut.reports.report_stats import ReportStatistics
from com.wisenut.reports.report_emotions import ReportEmotions
from com.wisenut.reports.report_trend import ReportTrend
from com.wisenut.reports.report_count import ReportCount
import sys
if __name__ == '__main__':
    seq         = sys.argv[1]
    regDt       = sys.argv[2]
    typeCd      = sys.argv[3]
    channels    = sys.argv[4]
    startDate   = sys.argv[5]
    endDate     = sys.argv[6]
    datasets    = sys.argv[7]
    projectSeq  = sys.argv[8]
    compareYn   = sys.argv[9]
    
    req = {
        "seq": seq,
        "reg_dt" : regDt,
        "type_cd" : typeCd,
        "channels" : channels,
        "start_date" : startDate,
        "end_date" : endDate,
        "datasets" : datasets,
        "project_seq" : projectSeq,
        "compare_yn" : compareYn
    }
    print(req)
    
    if req['type_cd']=='RSS': # 수집문서통계
        report = ReportStatistics(req)
    elif req['type_cd']=='RSE': # 감성분석
        report = ReportEmotions(req)
    elif req['type_cd']=='RTC': # 검색트렌드 - 조회수
        report = ReportCount(req)
    elif req['type_cd']=='RTT': # 검색트렌드 - 트렌드
        report = ReportTrend(req)
            
    req['excel_file_path'] = report.create_file_path()
    req['excel_file_nm'] = report.get_file_name()
    report.create_report(req)
    
'''
- **G70(1440)** : (2017년 7월 25일 ~ 2017년 08월 24일 : 8월. 총 1개월치)
- **스팅어(1659), C Class(1560), 3시리즈(1533)** : (2017년 7월 25일 ~ 2017년 10월 24일 : 8, 9, 10월. 총 3개월 치)
싼타페 

'''    