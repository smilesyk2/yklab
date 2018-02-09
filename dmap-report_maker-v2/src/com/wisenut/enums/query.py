# -*- coding: utf-8 -*- 
'''
Created on 2017. 6. 22.

@author: Holly
채널 코드 관리를 위한 Enum 클래스
'''
#from enum import Enum, auto
import re
from datetime import date, timedelta
import com.wisenut.dao.mariadbclient as mariadb

#class Query(Enum):
class Query():
    def __init__(self):
        pass
    
    
    
    
    def DATASET_COUNT_PER_DAY_IN_DOCUMENTS(self, params, compare=False):
        if not compare:
            query =  {
              "size" : 0,
              "query" : {
                 "bool" : {
                    "filter" : [
                       self.get_period_query(params['start_date'], params['end_date']),
                       self.get_project_seq_query(params['project_seq']),
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                 }
              },
              "aggs" : {
                "my_aggs1" : {
                  "date_histogram": {
                    "field": "doc_datetime",
                    "interval": "day"
                  },
                  "aggs" : {
                    "my_aggs2": {
                      "filters" : { "filters" : {}}
                    }
                  }    
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query =  {
              "size" : 0,
              "query" : {
                 "bool" : {
                    "filter" : [
                       {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                       self.get_project_seq_query(params['project_seq']),
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                 }
              },
              "aggs" : {
                "my_aggs1" : {
                  "date_range" : {
                    "field" : "doc_datetime",
                    "ranges": [
                      {
                        "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                        "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                      },
                      {
                       "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                       "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                       },
                      {
                       "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                       "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                       },
                      {
                       "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                       "to": end_date.strftime('%Y-%m-%dT23:59:59')
                       }
                    ]
                  },
                  "aggs" : {
                    "my_aggs2" : {
                      "filters" : { "filters" : {}}
                    }
                  }
                }
              }
            }
            
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
        
        # 검색 범위를 제한하는 데 쓰이는 쿼리
        should = [] 
        for datasetSeq in params['datasets'].split("^"):
            for keywordSet in self.get_dataset_query(datasetSeq): 
                should.append(keywordSet)
            # aggregation을 위해 filters에 추가할 쿼리
            query["aggs"]["my_aggs1"]["aggs"]["my_aggs2"]["filters"]["filters"][datasetSeq] = {
                                                                                                "bool" : {
                                                                                                    "should" : self.get_dataset_query(datasetSeq)
                                                                                                }
                                                                                            }
        
        return query
    
    
       
       
    def DATASET_COUNT_PER_DAY_IN_EMOTIONS(self, params, compare=False):
        if not compare:
            query =  {
              "size" : 0,
              "query" : {
                 "bool" : {
                    "filter" : [
                       self.get_period_query(params['start_date'], params['end_date']),
                       self.get_project_seq_query(params['project_seq']),
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                 }
              },
              "aggs" : {
                "my_aggs1" : {
                  "date_histogram": {
                    "field": "doc_datetime",
                    "interval": "day"
                  },
                  "aggs" : {
                    "my_aggs2" : {
                        "children" : {
                            "type" : "emotions"
                        },
                        "aggs": {
                            "my_aggs3": {
                              "filters" : { "filters" : {}}
                            }
                        }    
                    }
                  }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query =  {
              "size" : 0,
              "query" : {
                 "bool" : {
                    "filter" : [
                       {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59'),
                            }
                          }
                        },
                       self.get_project_seq_query(params['project_seq']),
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                 }
              },
              "aggs" : {
                "my_aggs1" : {
                  "date_range" : {
                    "field" : "doc_datetime",
                    "ranges": [
                      {
                        "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                        "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                      },
                      {
                       "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                       "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                       },
                      {
                       "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                       "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                       },
                      {
                       "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                       "to": end_date.strftime('%Y-%m-%dT23:59:59')
                       }
                    ]
                  },
                  "aggs" : {
                    "my_aggs2" : {
                        "children" : {
                            "type" : "emotions"
                        },
                        "aggs": {
                            "my_aggs3": {
                              "filters" : { "filters" : {}}
                            }
                        }
                    } 
                  }
                }
              }
            }
            
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
        
        # 검색 범위를 제한하는 데 쓰이는 쿼리
        should = [] 
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
            # aggregation을 위해 filters에 추가할 쿼리
            query["aggs"]["my_aggs1"]["aggs"]["my_aggs2"]["aggs"]["my_aggs3"]["filters"]["filters"][datasetSeq] = { "has_parent" : {"parent_type" : "documents",
                                                                                                                                    "query" :{
                                                                                                                                        "bool" : {
                                                                                                                                            "should" : self.get_dataset_query(datasetSeq)
                                                                                                                                        }
                                                                                                                }}}
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        # 2018.01.29 must -> filter로 변경.
        #query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
        
        
        
    
    def DATASET_OCCUPATIONS_PER_DEPTH1_IN_DOCUMENTS(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "filters" : {
                        "filters" : {}
                    },
                    "aggs" : {
                        "my_aggs2" : {
                            "terms" : {
                                "field" : "depth1_nm.keyword",
                                "size" : 1000
                            }
                        }
                    }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "date_range" : {
                        "field" : "doc_datetime",
                        "ranges": [
                          {
                            "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                            "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                          },
                          {
                           "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                           "to": end_date.strftime('%Y-%m-%dT23:59:59')
                           }
                        ]
                      },
                      "aggs" : {
                        "my_aggs2" : {
                            "filters" : { "filters" : {} },
                            "aggs" : {
                                "my_aggs3" : {
                                    "terms" : {
                                        "field" : "depth1_nm.keyword",
                                        "size" : 10
                                    }
                                }
                            }
                        }
                    }
                }
              }
            }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
            
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            # 검색할 때 범위를 제한.
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
            # aggregation의 결과를 데이터셋마다 갖고 오기 위한 쿼리.
            if not compare:
                query["aggs"]["my_aggs1"]["filters"]["filters"][datasetSeq] = { "bool" : { "should" : self.get_dataset_query(datasetSeq) } }
            else:
                query["aggs"]["my_aggs1"]["aggs"]["my_aggs2"]["filters"]["filters"][datasetSeq] = { "bool" : { "should" : self.get_dataset_query(datasetSeq) } }
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        # 2018.01.29 must -> filter로 변경.
        #query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
    
    
        
        
    def DATASET_OCCUPATIONS_PER_DEPTH1_IN_EMOTIONS(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "filters" : {
                        "filters" : {}
                    },
                    "aggs" : {
                        "my_aggs2" : {
                            "terms" : {
                                "field" : "depth1_nm.keyword",
                                "size" : 1000
                            },
                            "aggs" : {
                                "my_aggs3" : {
                                    "children" : {
                                        "type" : "emotions"
                                    }
                                }
                            }
                        }
                    }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "date_range" : {
                        "field" : "doc_datetime",
                        "ranges": [
                          {
                            "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                            "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                          },
                          {
                           "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                           "to": end_date.strftime('%Y-%m-%dT23:59:59')
                           }
                        ]
                      },
                      "aggs" : {
                        "my_aggs2" : {
                            "filters" : { "filters" : {} },
                            "aggs" : {
                                "my_aggs3" : {
                                    "terms" : {
                                        "field" : "depth1_nm.keyword",
                                        "size" : 10
                                    },
                                    "aggs" : {
                                        "my_aggs4" : {
                                            "children" : {
                                                "type" : "emotions"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
              }
            }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
            
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            # 검색할 때 범위를 제한.
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
            # aggregation의 결과를 데이터셋마다 갖고 오기 위한 쿼리.
            if not compare:
                query["aggs"]["my_aggs1"]["filters"]["filters"][datasetSeq] = { "bool" : { "should" : self.get_dataset_query(datasetSeq) } }
            else:
                query["aggs"]["my_aggs1"]["aggs"]["my_aggs2"]["filters"]["filters"][datasetSeq] = { "bool" : { "should" : self.get_dataset_query(datasetSeq) } }
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        # 2018.01.29 must -> filter로 변경.
        #query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
    
    
    
    
    def DEPTH1_CHANNEL_OCCUPATIONS(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
             "aggs" : {
                 "my_aggs1" : {
                     "terms" : {
                        "field" : "depth1_nm.keyword",
                        "size" : 1000
                    }
                  }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
                "aggs" : {
                  "my_aggs1" : {
                    "terms" : {
                        "field" : "depth1_nm.keyword",
                        "size" : 10
                    },
                    "aggs" : {
                     "my_aggs2" : {
                      "date_range" : {
                        "field" : "doc_datetime",
                        "ranges": [
                          {
                            "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                            "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                          },
                          {
                           "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                           "to": end_date.strftime('%Y-%m-%dT23:59:59')
                           }
                        ]
                      }
                     }
                    }
                  }
                }
            }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
                    
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
    
    
    
    
    
    def DEPTH2_CHANNEL_OCCUPATIONS(self, depth1_seq, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq']),
                        {
                         "term" : {
                            "depth1_seq" : depth1_seq
                         }
                        }
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
             "aggs" : {
                 "my_aggs1" : {
                     "terms" : {
                        "field" : "depth2_nm.keyword",
                        "size" : 1000
                    }
                  }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq']),
                        {
                         "term" : {
                            "depth1_seq" : depth1_seq
                         }
                        }
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
                "aggs" : {
                  "my_aggs1" : {
                    "terms" : {
                        "field" : "depth2_nm.keyword",
                        "size" : 10
                    },
                    "aggs" : {
                     "my_aggs2" : {
                      "date_range" : {
                        "field" : "doc_datetime",
                        "ranges": [
                          {
                            "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                            "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                          },
                          {
                           "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                           "to": end_date.strftime('%Y-%m-%dT23:59:59')
                           }
                        ]
                      }
                     }
                    }
                  }
                }
            }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
                    
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
    
    
        
    
    def TOPICS_LIST(self, params):
        query = {
          "size": 0,
          "query": {
            "bool" : {
                "filter" : [
                    {
                     "has_parent" :{
                        "parent_type" : "documents",
                        "query" : {
                            "bool" : {
                                "filter" : [
                                    self.get_period_query(params['start_date'], params['end_date']),
                                    self.get_project_seq_query(params['project_seq'])
                                ],
                                "must_not" : self.get_project_filter_query(params['project_seq'])
                            }
                        }
                      }
                    },
                    {
                     "term" : {
                       "topic_class.keyword" : "NN"
                     }
                    }
                ]
            }
          },
         "aggs" : {
           "my_aggs1" : {
             "terms" : {
                "field": "topic.keyword",
                "size" : 500
              },
              "aggs" : {
                "my_aggs2" : {
                  "terms" : {
                    "field" : "related_words.keyword",
                    "size" : 5
                  }
                }
              }
           }
         }
        }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'][0]['has_parent']['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
                    
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        query['query']['bool']['filter'][0]['has_parent']['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
    
    
    
    
    def TOPICS_VERBS_LIST(self, params):
        query = {
          "size": 0,
          "query": {
            "bool" : {
                "filter" : [
                    {
                     "has_parent" :{
                        "parent_type" : "documents",
                        "query" : {
                            "bool" : {
                                "filter" : [
                                    self.get_period_query(params['start_date'], params['end_date']),
                                    self.get_project_seq_query(params['project_seq'])
                                ],
                                "must_not" : self.get_project_filter_query(params['project_seq'])                            
                            }
                        }
                      }
                    },
                    {
                     "term" : {
                       "topic_class.keyword" : "VV"
                     }
                    }
                ]
            }
          },
          "aggs": {
            "my_aggs1": {
              "terms": {
                "field" : "topic.keyword",
                "size" : 500
              }
            }
          }
        }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'][0]['has_parent']['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
                    
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        query['query']['bool']['filter'][0]['has_parent']['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
    
    
        
        
    def DATASET_OCCUPATIONS_PER_DEPTH3_IN_DOCUMENTS(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "filters" : {
                        "filters" : {}
                    },
                    "aggs" :{
                      "my_aggs2" : {
                        "terms" : {
                            "script": "doc['depth1_nm.keyword'] + '>' + doc['depth2_nm.keyword'] + '>' + doc['depth3_nm.keyword']", 
                            "size" : 1000
                        }
                      }
                    }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "filters" : {
                        "filters" : {}
                    },
                    "aggs" : {
                      "my_aggs2" : {
                        "terms" : {
                            "script": "doc['depth1_nm.keyword'] + '>' + doc['depth2_nm.keyword'] + '>' + doc['depth3_nm.keyword']",
                            "size" : 1000
                        },
                        "aggs" :  {
                         "my_aggs3" : {
                          "date_range" : {
                            "field" : "doc_datetime",
                            "ranges": [
                              {
                                "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                              },
                              {
                               "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                               "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                               },
                              {
                               "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                               "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                               },
                              {
                               "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                               "to": end_date.strftime('%Y-%m-%dT23:59:59')
                               }
                            ]
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
                    
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
                
            query["aggs"]["my_aggs1"]["filters"]["filters"][datasetSeq] = { "bool" : { "should" : self.get_dataset_query(datasetSeq) } }
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        return query
    
        
        
    
        
    def DATASET_OCCUPATIONS_PER_DEPTH3_IN_EMOTIONS(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "filters" : {
                        "filters" : {}
                    },
                    "aggs" :{
                      "my_aggs2" : {
                        "terms" : {
                            "script": "doc['depth1_nm.keyword'] + '>' + doc['depth2_nm.keyword'] + '>' + doc['depth3_nm.keyword']", 
                            "size" : 1000
                        },
                        "aggs" : {
                            "my_aggs3" : {
                                "children" : {
                                    "type" : "emotions"
                                }
                            }
                        }
                      }
                    }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "filters" : {
                        "filters" : {}
                    },
                    "aggs" : {
                      "my_aggs2" : {
                        "terms" : {
                            "script": "doc['depth1_nm.keyword'] + '>' + doc['depth2_nm.keyword'] + '>' + doc['depth3_nm.keyword']",
                            "size" : 1000
                        },
                        "aggs" :  {
                         "my_aggs3" : {
                          "date_range" : {
                            "field" : "doc_datetime",
                            "ranges": [
                              {
                                "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                              },
                              {
                               "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                               "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                               },
                              {
                               "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                               "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                               },
                              {
                               "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                               "to": end_date.strftime('%Y-%m-%dT23:59:59')
                               }
                            ]
                          },
                          "aggs" : {
                            "my_aggs4" : {
                                "children" : {
                                    "type" : "emotions"
                                }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
                    
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
                
            query["aggs"]["my_aggs1"]["filters"]["filters"][datasetSeq] = { "bool" : { "should" : self.get_dataset_query(datasetSeq) } }
        
        # 2017.07.26 데이터셋 조건도 검색에 걸어줘야 날짜별 doc_count가 제한된 데이터셋 조건 내에서 합산되어 나옴.
        query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        return query
    
    
    
    
    def EMOTIONS_OCCUPATIONS(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter": [
                      self.get_period_query(params['start_date'], params['end_date']),
                      self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "children" : {
                        "type" : "emotions"
                    },
                    "aggs" : {
                        "my_aggs2" : {
                            "terms" : {
                                "field" : "emotion_type.keyword",
                                "size" : 10
                            }
                        }
                    }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter": [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "date_range" : {
                        "field" : "doc_datetime",
                        "ranges": [
                          {
                            "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                            "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                          },
                          {
                           "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                           "to": end_date.strftime('%Y-%m-%dT23:59:59')
                           }
                        ]
                    },
                    "aggs" : {
                        "my_aggs2" : {
                            "children" : {
                                "type" : "emotions"
                            },
                            "aggs" : { 
                                "my_aggs3" : {
                                    "terms" : {
                                        "field" : "emotion_type.keyword",
                                        "size" : 10
                                    } 
                                }
                            }
                        }
                    }
                }
              }
            }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
        
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
        
        
    def EMOTIONS_PROGRESS(self, params):
        query = {
          "size": 0,
          "query": {
             "bool" :{
                "filter" : [
                    self.get_period_query(params['start_date'], params['end_date']),
                    self.get_project_seq_query(params['project_seq'])
                ],
                "must_not" : self.get_project_filter_query(params['project_seq'])
              }
          },
         "aggs": {
            "my_aggs1":{
                "date_histogram" : {
                    "field" : "doc_datetime",
                    "interval" : "day"
                },
                "aggs": {
                    "my_aggs2" :{
                        "children" : {
                            "type" : "emotions"
                        },
                        "aggs" : {
                            "my_aggs3" : {
                                "terms": {
                                    "field": "emotion_type.keyword",
                                    "size": 10
                                },
                            }
                        },
                    }
                }
            }
          }
        }
        
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
        
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
    
    
    
        
    def EMOTIONS_PER_DEPTH1(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "terms" : {
                        "field" : "depth1_seq",
                        "size" : 10
                    },
                  "aggs": {
                    "my_aggs2" : {
                        "children" : {
                            "type" : "emotions"
                        },
                        "aggs" : {
                            "my_aggs3":{
                                "terms": {
                                   "field": "emotion_type.keyword",
                                   "size": 10
                                }
                            }
                        }
                    }
                  }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "date_range" : {
                        "field" : "doc_datetime",
                        "ranges": [
                          {
                            "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                            "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                          },
                          {
                           "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                           "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                           },
                          {
                           "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                           "to": end_date.strftime('%Y-%m-%dT23:59:59')
                           }
                        ]
                      },
                    "aggs" : {
                        "my_aggs2" : {
                          "terms" : {
                            "field" : "depth1_seq",
                            "size" : 100
                          },
                          "aggs" : {
                            "my_aggs3" : {
                                "children" : {
                                    "type" : "emotions"
                                },
                                "aggs": {
                                    "my_aggs4":{
                                        "terms": {
                                           "field": "emotion_type.keyword",
                                           "size": 10
                                        }
                                    }
                                }
                            }
                          }
                        }
                    }
                }
              }
            }
    
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
        
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        query['query']['bool']['filter'].append({ 'bool' : {'should': should}})
        
        return query
        
        
        
        
    def EMOTIONS_PER_CAUSES(self, params, compare=False):
        if not compare:
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        self.get_period_query(params['start_date'], params['end_date']),
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs": {
                "my_aggs1" :{
                    "terms" : {
                        "script": "doc['depth1_nm.keyword'] + '>' + doc['depth2_nm.keyword'] + '>' + doc['depth3_nm.keyword']",
                        "size" : 1000
                    },
                    "aggs" :{
                        "my_aggs2" : {
                            "children" : {
                                "type" : "emotions"
                            },
                            "aggs": {
                                "my_aggs3":{
                                    "terms": {
                                       "field": "conceptlevel1.keyword",
                                       "size": 1000
                                    },
                                    "aggs": {
                                        "my_aggs4":{
                                            "terms": {
                                               "field": "conceptlevel2.keyword",
                                               "size": 1000
                                            },
                                            "aggs": {
                                                "my_aggs5":{
                                                    "terms": {
                                                       "field": "conceptlevel3.keyword",
                                                       "size": 1000
                                                    },
                                                    "aggs": {
                                                        "my_aggs6":{
                                                            "terms": {
                                                               "field": "emotion_type.keyword",
                                                               "size": 10
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
              }
            }
        else:
            start_date = date(int(params['start_date'][0:4]), int(params['start_date'][5:7]), int(params['start_date'][8:10]))
            end_date = date(int(params['end_date'][0:4]), int(params['end_date'][5:7]), int(params['end_date'][8:10]))
            time_interval = end_date-start_date+timedelta(days=1)
            
            query = {
              "size": 0,
              "query": {
                "bool" : {
                    "filter" : [
                        {
                         "range" : {
                            "doc_datetime" : {
                                "from" : (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                                "to" : end_date.strftime('%Y-%m-%dT23:59:59')
                            }
                          }
                        },
                        self.get_project_seq_query(params['project_seq'])
                    ],
                    "must_not" : self.get_project_filter_query(params['project_seq'])
                }
              },
              "aggs" : {
                "my_aggs1" : {
                    "date_range" : {
                    "field" : "doc_datetime",
                    "ranges": [
                      {
                        "from": (start_date-time_interval*3).strftime('%Y-%m-%dT00:00:00'),
                        "to": (end_date-time_interval*3).strftime('%Y-%m-%dT23:59:59')
                      },
                      {
                       "from": (start_date-time_interval*2).strftime('%Y-%m-%dT00:00:00'),
                       "to": (end_date-time_interval*2).strftime('%Y-%m-%dT23:59:59')
                       },
                      {
                       "from": (start_date-time_interval*1).strftime('%Y-%m-%dT00:00:00'),
                       "to": (end_date-time_interval*1).strftime('%Y-%m-%dT23:59:59')
                       },
                      {
                       "from": start_date.strftime('%Y-%m-%dT00:00:00'),
                       "to": end_date.strftime('%Y-%m-%dT23:59:59')
                       }
                    ]
                  },
                  "aggs": {
                    "my_aggs2" :{
                        "terms" : {
                            "script": "doc['depth1_nm.keyword'] + '>' + doc['depth2_nm.keyword'] + '>' + doc['depth3_nm.keyword']",
                            "size" : 1000
                        },
                        "aggs": {
                            "my_aggs3" : {
                                "children" : {
                                    "type" : "emotions"
                                },
                                "aggs": {
                                    "my_aggs4":{
                                        "terms": {
                                           "field": "conceptlevel1.keyword",
                                           "size": 1000
                                        },
                                        "aggs": {
                                            "my_aggs5":{
                                                "terms": {
                                                   "field": "conceptlevel2.keyword",
                                                   "size": 1000
                                                },
                                                "aggs": {
                                                    "my_aggs6":{
                                                        "terms": {
                                                           "field": "conceptlevel3.keyword",
                                                           "size": 1000
                                                        },
                                                        "aggs": {
                                                            "my_aggs7":{
                                                                "terms": {
                                                                   "field": "emotion_type.keyword",
                                                                   "size": 10
                                                                }                                                            
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } 
                        }
                    }
                  }
                }
              }
            }
    
        if self.get_channel_query(params['channels']):
            query['query']['bool']['filter'].append(self.get_channel_query(params['channels']))
        
        should = []        
        for datasetSeq in params['datasets'].split("^"):
            for keyword_set in self.get_dataset_query(datasetSeq): 
                should.append(keyword_set)
        
        query['query']['bool']['filter'].append({'bool' : {'should': should}})
        
        
        return query
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    
    def get_period_query(self, startDate, endDate):
        '''
        str_start_date = self.params['start_date'] if self.params["start_date"] else "1900-01-01T00:00:00"
        str_end_date = self.params['end_date'] if self.params["end_date"] else "2100-12-31T23:59:59"
        if self.params['compare_yn']=='Y':
            # 기준날짜
            start_date = date(int(self.params['start_date'][0:4]), int(self.params['start_date'][5:7]), int(self.params['start_date'][8:10]))
            end_date = date(int(self.params['end_date'][0:4]), int(self.params['end_date'][5:7]), int(self.params['end_date'][8:10]))
            time_interval = end_date-start_date
            # 비교 날짜들(1time_interval before)
            this_end_date = end_date - (time_interval+timedelta(days=1))*3 # 곱해진 간격만큼 이전 날짜를 구함
            
            str_start_date = (this_end_date-time_interval).strftime('%Y-%m-%dT00:00:00')
        ''' 
            
        return {
            "range": {
                "doc_datetime": {
                    "gte" : startDate if startDate else "1900-01-01T00:00:00",
                    "lte" : endDate if endDate else "2100-12-31T23:59:59"
                } 
            }
        }
        
    def get_project_seq_query(self, projectSeq):
        return {
            "term" : {
                "project_seq" : projectSeq 
            }
        }
        
    def get_channel_query(self, channels):
        if not channels or channels == "all":
            return None
        else:
            query = ''
            for c in re.sub(";$", "", channels).split(";"):
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
            
            
    
    def get_dataset_query(self, datasetSeq):
        dataset_keyword_list = mariadb.get_include_keywords(datasetSeq) # dataset 시퀀스로 dataset_keyword 조회
        
        keyword_sets_should = []
        for result in dataset_keyword_list:
            this_must = {}
            this_must_not = []
            
            keyword = result["keyword"].strip() if result["keyword"] else ""
            subkeywords = result["sub_keywords"].strip() if result["sub_keywords"] else ""
            title_filter_keywords = result['title_filter_keywords'].strip() if result['title_filter_keywords'] else ""
            content_filter_keywords = result['content_filter_keywords'].strip() if result['content_filter_keywords'] else ""
            url_filter = result['filter_urls'].strip() if result['filter_urls'] else ""
            # 2017.11.23 추가
            standard_keyword = result['standard_keyword'].strip() if result['standard_keyword'] else ""
            standard_title_filter_keywords = result['standard_title_filter_keywords'].strip() if result['standard_title_filter_keywords'] else ""
            standard_content_filter_keywords = result['standard_content_filter_keywords'].strip() if result['standard_content_filter_keywords'] else ""
            
            #1.키워드 세팅
            #1-1. 키워드 세팅
            if len(subkeywords)>0:
                keyword += "," + subkeywords
                
            keyword = keyword.replace(",", " ")
                
            #1-5. standard 키워드 세팅(형태소 분석X)
            standard_keyword = standard_keyword.replace(",", " ")
            
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
                        "query" : "\""+re.sub("\s\s", " ", re.sub("[\\^!@#\\$%&\\*\\(\\)\\-_\\+=`~\\.\\?\\/]", " ", re.sub(",$", "", title_filter_keywords)).replace(",", "\" OR \""))+"\"",
                        "default_operator" : "AND"
                    }
                })
            
            #2-3. 본문 필터 쿼리
            if len(content_filter_keywords)>0:
                this_must_not.append({
                    "query_string" : {
                        "fields": ["doc_content"],
                        "query" : "\""+re.sub("\s\s", " ", re.sub("[\\^!@#\\$%&\\*\\(\\)\\-_\\+=`~\\.\\?\\/]", " ", re.sub(",$", "", content_filter_keywords)).replace(",", "\" OR \""))+"\"",
                        "default_operator" : "AND"
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
                    
            #2-5. 키워드 쿼리(형태소분석X)
            if len(standard_keyword.strip())>0:
                this_must = {
                    "query_string": {
                        "fields": ["doc_title", "doc_content"],
                        "query" : standard_keyword, # (신라면 농심 nongshim) OR (辛라면 농심) OR (푸라면 놈심)
                        "default_operator" : "AND",
                        "analyzer" : "standard"
                    }
                }   
                    
            #2-6. 제목  필터 쿼리(형태소분석 X)
            if len(standard_title_filter_keywords)>0:
                this_must_not.append({
                    "query_string" : {
                        "fields": ["doc_title"],
                        "query" : "\""+re.sub("\s\s", " ", re.sub("[\\^!@#\\$%&\\*\\(\\)\\-_\\+=`~\\.\\?\\/]", " ", re.sub(",$", "", standard_title_filter_keywords)).replace(",", "\" OR \""))+"\"",
                        "default_operator" : "AND",
                        "analyzer" : "standard"
                    }
                })
                
            #2-7. 내용  필터 쿼리(형태소분석 X)
            if len(standard_content_filter_keywords)>0:
                this_must_not.append({
                    "query_string" : {
                        "fields": ["doc_content"],
                        "query" : "\""+re.sub("\s\s", " ", re.sub("[\\^!@#\\$%&\\*\\(\\)\\-_\\+=`~\\.\\?\\/]", " ", re.sub(",$", "", standard_content_filter_keywords)).replace(",", "\" OR \""))+"\"",
                        "default_operator" : "AND",
                        "analyzer" : "standard"
                    }
                })
                    
            if len(bool_should)>0:
                this_must_not.append({
                    "bool" : {
                        "should" : bool_should
                    }
                })
            
            keyword_set = { "bool" : {} }
            if this_must:
                keyword_set["bool"]["must"] = this_must
                
            if this_must_not:
                keyword_set["bool"]["must_not"] = this_must_not
            
            keyword_sets_should.append(keyword_set)
            
        return keyword_sets_should
        
        
        
    
    def get_documents_query(self, params):
        query = {
           "query" : {
               "bool" : {
                    "filter" : None,
                    "must_not" : self.get_project_filter_query(params['project_seq']),
                    "minimum_should_match" : 1,
                    "should" : None,
                }
            }
        }
        
        filter = []
        # 프로젝트 시퀀스 포함
        filter.append(self.get_project_seq_query(params['project_seq']))
    
        # 대상 채널
        if "channels" in params and params["channels"] and params["channels"] != 'all':
            filter.append(self.get_channel_query(params["channels"]))
    
        # 원문의 대상 기간
        if "start_date" in params or "end_date" in params:
            filter.append(self.get_period_query(params['start_date'], params['end_date']))
     
             
        # 데이터셋의 포함 키워드
        should = []
        if "datasets" in params and params["datasets"]: # 신라면,삼양라면,안성탕면
            if len(params["datasets"].split("^"))>1:
                for datasetSeq in params["datasets"].split("^"):
                    should.append(self.get_dataset_query(datasetSeq))
        
        query["query"]['bool']["filter"] = filter
        query["query"]['bool']["should"] = should if len(should)>0 else self.get_dataset_query(params["datasets"])
        
        return query
    
    
    
    
    def get_emotions_query(self, params):
        query = {
            "query" : {
                "bool" : {
                    "filter" :[
                        {
                          "term" : {
                            "relation_name" : "emotions"
                          }
                        },
                        {
                          "has_parent" : {
                            "parent_type" : "documents",
                               "query" : {
                                    "bool" : {
                                        "must_not" : self.get_project_filter_query(params['project_seq']),
                                        "minimum_should_match" : 1,
                                        "should" : None,
                                        "filter" : None
                                    }
                                }
                            }
                        }
                    ]
                }
            }
        }
        
        filter = []
        # 프로젝트 시퀀스 포함
        filter.append(self.get_project_seq_query(params['project_seq']))
    
        # 대상 채널
        if "channels" in params and params["channels"] and params["channels"] != 'all':
            filter.append(self.get_channel_query(params['channels']))
    
        # 원문의 대상 기간
        if "start_date" in params or "end_date" in params:
            filter.append(self.get_period_query(params["start_date"], params["end_date"]))
             
        # 데이터셋의 포함 키워드
        should = []
        if "datasets" in params and params["datasets"]: # 신라면,삼양라면,안성탕면
            if len(params["datasets"].split("^"))>1:
                for datasetSeq in params["datasets"].split("^"):
                    should.append(self.get_dataset_query(datasetSeq))
    
        query["query"]['bool']['filter'][1]["has_parent"]["query"]["bool"]["filter"] = filter
        query["query"]['bool']['filter'][1]["has_parent"]["query"]["bool"]["should"] = should if len(should)>0 else self.get_dataset_query(params["datasets"])
            
            
        return query
    
    
    
    
    
    def get_project_filter_query(self, projectSeq):
        project_filter_keywords = mariadb.get_project_filter_keywords(projectSeq)
        
        project_title_filter = project_filter_keywords['title_filter_keywords'].strip() if project_filter_keywords and 'title_filter_keywords' in project_filter_keywords else ''
        project_content_filter = project_filter_keywords['content_filter_keywords'].strip() if project_filter_keywords and 'content_filter_keywords' in project_filter_keywords else ''
        project_url_filter = project_filter_keywords['filter_urls'].strip() if project_filter_keywords and 'filter_urls' in project_filter_keywords else ''
        
        must_not = []
        
        if len(project_title_filter)>0:
            must_not.append({
                "query_string" : {
                    "fields": ["doc_title"],
                    "query" : "\""+re.sub("\s\s", " ", re.sub("[\\^!@#\\$%&\\*\\(\\)\\-_\\+=`~\\.\\?\\/]", " ", re.sub(",$", "", project_title_filter)).replace(",", "\" OR \""))+"\"",
                    "default_operator" : "AND"
                }
            })

            #2-3. 본문 필터 쿼리
        if len(project_content_filter)>0:
            must_not.append({
                "query_string" : {
                    "fields": ["doc_content"],
                    "query" : "\""+re.sub("\s\s", " ", re.sub("[\\^!@#\\$%&\\*\\(\\)\\-_\\+=`~\\.\\?\\/]", " ", re.sub(",$", "", project_content_filter)).replace(",", "\" OR \""))+"\"",
                    "default_operator" : "AND"
                }
            })

        #2-4.URL 필터 쿼리
        for url in project_url_filter.strip().split(","):
            if len(url)>0:
                must_not.append({
                    "match_phrase" : {
                        "doc_url" : url
                    }
                })
                
        return must_not
