# -*- coding: utf-8 -*- 
'''
Created on 2017. 6. 13.

@author: Holly
'''
import http.client as hc
import logging
import json, re
import com.wisenut.dao.mariadbclient as mariadb
from com.wisenut.enums.query import Query
import traceback
from com.wisenut import myLogger

############# logger 세팅
logger = myLogger.getMyLogger("esclient", False, True, logging.DEBUG)

############# Elasticsearch 정보 세팅
#es_ip = "ec2-13-124-161-198.ap-northeast-2.compute.amazonaws.com"
es_ip="211.39.140.96"
es_port = 9200
es_conn = hc.HTTPConnection(es_ip, es_port, timeout=300)

attr_dict = {
    "brand" : "브랜드",
    "region" : "지명",
    "person" : "인명"
}



class EsRejectedExecutionException(Exception):
    pass



def clear_scroll(scroll_id):
    try:
        es_conn = hc.HTTPConnection(es_ip, es_port, timeout=300)
        es_conn.request("DELETE", "/_search/scroll", json.dumps({"scroll_id":scroll_id}), { "Content-Type" : "application/json" })
        logger.info("[clear_scroll] done.")
    except OSError as oserror:
        ex = traceback.format_exc()
        logger.error("[clear_scroll] OS error : %s. Traceback >> %s " % (str(oserror), ex))
    except:
        ex = traceback.format_exc()
        logger.error("[clear_scroll] error. Traceback >> %s " % ex)
        
        
        
def getNumberOfRejectedTasks(name):
    numberOfRejectedTasks = -1
          
    if name is None or name == '':
        print("No name entered.")
        return numberOfRejectedTasks
        
    try:
        es_conn = hc.HTTPConnection(es_ip, es_port, timeout=300)
        es_conn.request("GET", "/_cat/thread_pool/"+name+"?v&h=id,name,queue,queue_size,active,rejected,completed,keep_alive,min,max,node_name&s=name&format=json&pretty","", { "Content-Type" : "application/json" })
        
        threadPoolResult = json.loads(es_conn.getresponse().read())
        
        for result in threadPoolResult:
            if 'name' in result and result['name']==name:
                numberOfRejectedTasks = int(result['rejected'])
                break
                
        logger.info("[getThreadPool] numberOfRejectedTasks : %d" % numberOfRejectedTasks)
    except OSError as oserror:
        ex = traceback.format_exc()
        logger.error("[getThreadPool] OS error : %s. Traceback >> %s " % (str(oserror), ex))
    except:
        ex = traceback.format_exc()
        logger.error("[getThreadPool] error. Traceback >> %s " % ex)

    return numberOfRejectedTasks





def get_topic_attr(topic):
    request = {
        "query": {
            "query_string": {
              "query": "(_type:brand name:({})) OR (_type:region  name:({})) OR (_type:person name:({}))".format(topic, topic, topic),
              "default_operator": "AND"
            }
          }
    }
    
    es_conn.request("GET", "/dmap/brand/_search", json.dumps(request), {"Content-type" : "application/json"})
    result = json.loads(es_conn.getresponse().read())
    
    if "hits" in result and result["hits"]["total"] >0:
        return attr_dict[result["hits"]["hits"][0]["_type"]]
    else:
        return ""
    
    
    
def get_aggregations(query, req, index):
    #logger.info("[get_aggregations] query >>> %s" % json.dumps(query))
       
    es_conn = hc.HTTPConnection(es_ip, es_port, timeout=300)
    es_conn.request("GET", "/"+index+"/doc/_search", json.dumps(query), {"Content-type" : "application/json"})
    
    result = es_conn.getresponse().read()
    
    if 'hits' not in json.loads(result):
        logger.error("[get_aggregations] result >>> %s" % result)
    
    return json.loads(result)  
    
    
    
    
def get_count(uri, request):
    es_conn = hc.HTTPConnection(es_ip, es_port, timeout=60)
    es_conn.request("POST", uri, json.dumps(request), {"Content-type" : "application/json"})
    result = es_conn.getresponse().read()

    if 'count' in json.loads(result):
        return json.loads(result)['count']
    else:
        logger.error("[get_count] %s"  % str(result))
        return -1
    
    



def get_list(uri, request, size, scroll_id=None):
    if scroll_id is None:
        es_uri = uri + "?scroll=1d"
        request['size'] = size
    else:
        es_uri = "/_search/scroll"
        request = {
            "scroll" : "1d",
            "scroll_id" : scroll_id
        }

    es_conn = hc.HTTPConnection(es_ip, es_port, timeout=60)
    es_conn.request("POST", es_uri, json.dumps(request), {"Content-type" : "application/json"})
    result = es_conn.getresponse().read()

    if 'hits' in json.loads(result):
        logger.debug("[get_list] result['hits']['total'] >>> %d" % int(json.loads(result)['hits']['total']) )
    else:
        logger.debug("[get_list] result ::: " + str(result))

    return json.loads(result)


    
'''        
    
def get_documents(params, size, index, scroll_id=None):
    queryObj = Query(params)
    
    if not scroll_id:
        es_uri = "/"+index+"/doc/_search?scroll=1d"
        request = {
            "size" : size,
           "query" : {
               "bool" : {
                    "must" : []
                }
            }
        } 
    else:
        es_uri = "/_search/scroll"
        request = {
            "scroll" : "1d",
           "scroll_id" : scroll_id
        } 
    
    must = []
    # 프로젝트 시퀀스 포함
    must.append(get_project_seq_query(params))
    
    # 대상 채널
    if "channels" in params and params["channels"] and params["channels"] != 'all':
        must.append(get_channel_query(params))
     
    # 대상 기간   
    if "start_date" in params and "end_date" in params:
        must.append(get_period_query(params))

    # 데이터셋의 포함 키워드
    if "datasets" in params and params["datasets"]: # 신라면,삼양라면,안성탕면
        if len(params["datasets"].split("^"))>1:
            should = []
            for dataset in params["datasets"].split("^"):
                should.append(queryObj.get_dataset_query(params['project_seq'], dataset))
            
            must.append({
                "bool" : {
                    "should" : should
                }
            })
        else:
            must.append(queryObj.get_dataset_query(params['project_seq'], params["datasets"]))
      
        
    # elif params["type_cd"] == "CCT002": # 소셜모니터링-문서통계
    # elif params["type_cd"] == "CCT003": # 소셜모니터링-감성분석
    # .....
    # 코드별로 request 필요한 형태로 변경해서 추가
    
    if "query" in request:
        request["query"]["bool"]["must"] = must
        
    logger.debug("get_documents() ==> request : " )
    for k,v in request.items():
        logger.debug("\t{} : {}".format(k,v))
    
    es_conn = hc.HTTPConnection(es_ip, es_port, timeout=300)
    es_conn.request("POST", es_uri, json.dumps(request), {"Content-type" : "application/json"})
    result = es_conn.getresponse().read()
    
    if 'hits' in json.loads(result):
        logger.debug("[get_documents] result['hits']['total'] >>> %d" % int(json.loads(result)['hits']['total']) )
    else:
        logger.debug("[get_documents] result ::: " + str(result))
    
    return json.loads(result) 
'''


def get_period_query(params):
    return {
            "range": {
                "doc_datetime": {
                    "gte" : params["start_date"] if params["start_date"] else "1900-01-01T00:00:00",
                    "lte" : params["end_date"] if params["end_date"] else "2100-12-31T23:59:59"
                } 
            }
        }


    
def get_project_seq_query(params):
    return {
        "term" : {
            "project_seq" : params['project_seq'] 
        }
    }


   
def get_channel_query(params):
    channel = re.sub(";$", "", params["channels"]) # 1^5,6,7,8;2^11,12,13,14
    
    if not channel or channel == "all":
        return None
    else:
        query = ''
        for c in channel.split(";"):
            depth1_seq = c.split("^")[0]
            
            query += "("
            query += "depth1_seq:"+depth1_seq
            if len(c.split("^"))>1:
                query += " AND depth2_seq:("+" OR ".join(c.split("^")[1].split(","))+")"
            query += ")"
            
            query += " OR "
            
        return {
                "query_string": {
                    "query": re.compile(" OR $").sub("", query)
                }
            }
   
''' 
def get_dataset_query(project_seq, dataset_seq):
    dataset_keyword_list = mariadb.get_include_keywords(dataset_seq) # dataset 시퀀스로 dataset_keyword 조회
    project_filter_keywords = mariadb.get_project_filter_keywords(project_seq)
    
    project_title_filter = project_filter_keywords['title_filter_keywords'] if project_filter_keywords and 'project_filter_keywords' in project_filter_keywords else ''
    project_content_filter = project_filter_keywords['content_filter_keywords'] if project_filter_keywords and 'content_filter_keywords' in project_filter_keywords else ''
    project_url_filter = project_filter_keywords['filter_urls'] if project_filter_keywords and 'filter_urls' in project_filter_keywords else ''
    
    keyword_sets_should = []
    for result in dataset_keyword_list:
        this_must = {}
        this_must_not = []
        
        keyword = result["keyword"].strip()
        subkeywords = result["sub_keywords"].strip()
        title_filter_keywords = result['title_filter_keywords'].strip()
        content_filter_keywords = result['content_filter_keywords'].strip()
        url_filter = result['filter_urls'].strip()
        
        #1.키워드 세팅
        #1-1. 키워드 세팅
        if len(subkeywords)>0:
            keyword += "," + subkeywords
            keyword = keyword.replace(",", " ")
        
        #1-2. 제목 필터 키워드 
        if len(project_title_filter.strip())>0:
            title_filter_keywords += "," + project_title_filter
                
        #1-3. 본문 필터 키워드
        if len(project_content_filter)>0:
            content_filter_keywords += "," + project_content_filter
                
        #1-4. URL 필터
        if len(project_url_filter)>0:
            url_filter += "," + project_url_filter
            
        #2.쿼리 세팅
        #2-1. 키워드 쿼리
        if len(keyword.strip())>0:
            this_must = {
                "query_string": {
                    "fields": ["doc_title", "doc_content"],
                    "query" : keyword, # (신라면 농심 nongshim) OR (辛라면 농심) OR (푸라면 놈심)
                    "default_operator" : "AND"
                }
            }        
        
        #2-2. 제목 필터 쿼리
        if len(title_filter_keywords)>0:
            this_must_not.append({
                "query_string" : {
                    "fields": ["doc_title"],
                    "query" : "("+re.sub("\\^\\^", " AND ", re.sub(",$", "", result['title_filter_keywords'])).replace(",",") (")+")"
                }
            })
        
        #2-3. 본문 필터 쿼리
        if len(content_filter_keywords)>0:
            this_must_not.append({
                "query_string" : {
                    "fields": ["doc_content"],
                    "query" : "("+re.sub("\\^\\^", " AND ", re.sub(",$", "", result['content_filter_keywords'])).replace(",",") (")+")"
                }
            })
            
        #2-4.URL 필터 쿼리
        bool_should = []
        for url in url_filter.split(","):
            if len(url)>0:
                bool_should.append({
                    "match_phrase" : {
                        "doc_url" : url
                    }
                })
                
        if len(bool_should)>0:
            this_must_not.append({
                "bool" : {
                    "should" : bool_should
                }
            })
        
        keyword_sets_should.append({
            "bool" : {
                "must" : this_must,
                "must_not" : this_must_not
            }
        })
        
    return {
        "bool" : {
            "should" : keyword_sets_should
        }
    }
'''