# -*- coding: utf-8 -*- 
'''
Created on 2018. 1. 16.

@author: Holly
'''
from logging.handlers import TimedRotatingFileHandler
from logging import StreamHandler
import logging
import os


def getMyLogger(processName, hasConsoleHandler=True, hasRotatingFileHandler=False, logLevel=logging.INFO, logPath=""):
    loggerQualName = 'logger-%s'%processName
    
    logger = logging.getLogger(loggerQualName)
    simpleFormatter = logging.Formatter('[%(levelname)s][%(asctime)s] %(message)s')
    
    
    if hasConsoleHandler:
        consoleHandler = StreamHandler()

        consoleHandler.setFormatter( simpleFormatter )

        logger.addHandler( consoleHandler )
    
    
    
    if hasRotatingFileHandler:
        timedRotatingFileHandler = TimedRotatingFileHandler(os.path.join(logPath, "log/%s.log"%processName), when="midnight")
        
        logFormatter = logging.Formatter('[%(levelname)s][%(asctime)s] %(message)s')
        timedRotatingFileHandler.setFormatter( logFormatter )

        logger.addHandler( timedRotatingFileHandler )
        
        
    logger.setLevel( logLevel )
    
    return logger