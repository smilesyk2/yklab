@echo off
set seq=%1
set regDt=%2
set typeCd=%3
set channels=%4
set startDate=%5
set endDate=%6
set datasets=%7
set projectSeq=%8
set compareYn=%9

cmd /c "set PYTHONPATH=C:\ProgramData\Anaconda3\Lib;E:\dev\pythonworks\dmap-report_maker-v2\src && python E:\dev\pythonworks\dmap-report_maker-v2\src\com\wisenut\excel_maker_test.py %seq% %regDt% %typeCd% %channels% %startDate% %endDate% %datasets% %projectSeq% %compareYn%"
