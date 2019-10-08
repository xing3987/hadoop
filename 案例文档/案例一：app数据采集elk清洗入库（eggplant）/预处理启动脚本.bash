#!/bin/bash
day_str=`date +'%Y-%m-%d'`

inpath=/app-log-data/data/$day_str
outpath=/app-log-data/clean/$day_str

echo "准备清洗$day_str 的数据......"

/root/apps/hadoop-2.8.1/bin/hadoop jar /root/data-clean.jar cn.edu360.app.log.mr.AppLogDataClean $inpath $outpath