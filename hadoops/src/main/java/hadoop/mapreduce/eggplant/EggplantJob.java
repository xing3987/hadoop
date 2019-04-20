package hadoop.mapreduce.eggplant;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Map;

/**
 * 模拟茄子快传app,用户数据分析,数据清洗,筛选掉无用数据
 */
public class EggplantJob {
    private static Logger logger= LoggerFactory.getLogger(EggplantJob.class);
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.exit(10);
        }
        //获取程序的输入和输出目录
        /*String inputPath = "G:\\datas\\input\\eggplant";
        String outputPath = "G:\\datas\\output\\eggplant";*/
        String inputPath = args[0];
        String outputPath = args[1];

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(EggplantJob.class);

        //也可以不要reduce阶段
        //job.setMapperClass(EggplantMapper.class);
        //job.setMapOutputKeyClass(Eggplant.class);
        //job.setMapOutputValueClass(NullWritable.class);
        //job.setNumReduceTasks(0);
        //mapper阶段直接输出key，value为null

        job.setMapperClass(EggplantMapper.class);
        job.setReducerClass(EggplantReducer.class);
        job.setMapOutputKeyClass(Eggplant.class);
        job.setMapOutputValueClass(Eggplant.class);
        job.setOutputKeyClass(Eggplant.class);
        job.setOutputValueClass(NullWritable.class);

        job.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        job.waitForCompletion(true);
    }

    public static class EggplantMapper extends Mapper<LongWritable, Text, Eggplant, Eggplant> {
        private Eggplant eggplant = new Eggplant();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (line == null || line.equals("")) {
                return;
            }
            Map<String, Object> parse = (Map) JSONObject.parse(line);
            Map<String, String> map = (Map) parse.get("header");
            Boolean flag = validateAll(map);
            if(!flag){
                return;
            }
            if(map.get("os_name").equalsIgnoreCase("android")){
                eggplant.setUser_id(map.get("android_id"));
            }else{
                eggplant.setUser_id(map.get("device_id"));
            }
            eggplant.setCid_sn(map.get("cid_sn"));
            eggplant.setMobile_data_type(map.get("mobile_data_type"));
            eggplant.setOs_ver(map.get("os_ver"));
            eggplant.setMac(map.get("mac"));
            eggplant.setResolution(map.get("resolution"));
            eggplant.setCommit_time(map.get("commit_time"));
            eggplant.setSdk_ver(map.get("sdk_ver"));
            eggplant.setDevice_id(map.get("device_id_type"));
            eggplant.setCity(map.get("city"));
            eggplant.setAndroid_id(map.get("android_id"));
            eggplant.setDevice_model(map.get("device_model"));
            eggplant.setCarrier(map.get("carrier"));
            eggplant.setPromotion_channel(map.get("promotion_channel"));
            eggplant.setApp_ver_name(map.get("app_ver_name"));
            eggplant.setImei(map.get("imei"));
            eggplant.setApp_ver_code(map.get("app_ver_code"));
            eggplant.setPid(map.get("pid"));
            eggplant.setNet_type(map.get("net_type"));
            eggplant.setDevice_id(map.get("device_id"));
            eggplant.setApp_device_id(map.get("app_device_id"));
            eggplant.setRelease_channel(map.get("release_channel"));
            eggplant.setCountry(map.get("country"));
            eggplant.setTime_zone(map.get("time_zone"));
            eggplant.setOs_name(map.get("os_name"));
            eggplant.setManufacture(map.get("manufacture"));
            eggplant.setCommit_id(map.get("commit_id"));
            eggplant.setApp_token(map.get("app_token"));
            eggplant.setAccount(map.get("account"));
            eggplant.setApp_id(map.get("app_id"));
            eggplant.setBuild_num(map.get("build_num"));
            eggplant.setLanguage(map.get("language"));
            context.write(eggplant,eggplant);
        }
    }

    public static class EggplantReducer extends Reducer<Eggplant, Eggplant, Eggplant, NullWritable> {
        @Override
        protected void reduce(Eggplant key, Iterable<Eggplant> values, Context context) throws IOException, InterruptedException {
            context.write(key,NullWritable.get());
        }
    }

    /**
     * 筛选出没有用的数据过滤掉
     * @param map
     * @return
     */
    private static Boolean validateAll(Map<String,String> map) {
        if (map == null) {
            return false;
        }
        try{
            validateRequire(map.get("cid_sn"));
            validateRequire(map.get("mac"));
            validateRequire(map.get("commit_time"));
            validateRequire(map.get("sdk_ver"));
            validateRequire(map.get("device_id_type"));
            validateRequire(map.get("city"));
            validateRequire(map.get("device_model"));
            validateRequire(map.get("app_ver_name"));
            validateRequire(map.get("imei"));
            validateRequire(map.get("app_ver_code"));
            validateRequire(map.get("device_id"));
            validateRequire(map.get("release_channel"));
            validateRequire(map.get("country"));
            validateRequire(map.get("time_zone"));
            validateRequire(map.get("os_name"));
            if(map.get("os_name").equalsIgnoreCase("android")){
                validateRequire(map.get("android_id"));
            }
            validateRequire(map.get("commit_id"));
            validateRequire(map.get("app_token"));
            validateRequire(map.get("app_id"));
            validateRequire(map.get("language"));
        }catch (Exception e){
            logger.debug(e.getMessage());
            return false;
        }
        return true;
    }

    private static void validateRequire(String data) throws Exception{
        if (data == null || data.trim().equals("")) {
            throw new Exception("validate false.useless data.");
        }
    }

}
