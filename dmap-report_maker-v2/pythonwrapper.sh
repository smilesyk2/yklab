seq=$1
regDt=$2
typeCd=$3
channels=$4
startDate=$5
endDate=$6
datasets=$7
projectSeq=$8
compareYn=$9

PYTHONPATH="C/ProgramData/Anaconda3/Lib:E/dev/pythonworks/dmap-report_maker-v2/src" python "E/dev/pythonworks/dmap-report_maker-v2/src/com/wisenut/excel_maker_test.py" $seq $regDt $typeCd $channels $startDate $endDate $datasets $projectSeq $compareYn
