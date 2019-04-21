#!/bin/bash
#clean yestoday is data
day_str=`date -d '-1 day' +'%Y-%m-%d'`

inpath=/flume_log/$day_str
outpath=/eggplant/$day_str

echo "准备清洗$day_str 的数据......"

#clean data
/root/data/hadoop/hadoop-2.6.5/bin/hadoop jar /data/hadoopJob-jar-with-dependencies.jar hadoop.mapreduce.eggplant.EggplantJob $inpath $outpath

#add to hive
ALTER TABLE ods_app_log ADD PARTITION (dt = '$outpath') location '/eggplant/$outpath';