package com.link2map.oracle.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import oracle.spatial.util.SampleShapefileToJGeomFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShpFileUtil {

    public static Map<String,String> iniAndGet(){
        ComboPooledDataSource d = DBConnectionPool.getPool("db172");
        Properties acoountPro = d.getProperties();
        String url = d.getJdbcUrl();
        url = url.replace(" ","");//去掉空格
        String host = getIpFrom(url);
        int ipIndex = url.indexOf(host);
        String[] arr = url.substring(host.length()+ipIndex+1,url.length()).split("/");
        String port = arr[0];
        String sid = arr[1];
        String userName = acoountPro.getProperty("user");
        String passWd = acoountPro.getProperty("password");
        Map<String,String> map = new HashMap<>();
        map.put("-h",host);
        map.put("-p",port);
        map.put("-s",sid);
        map.put("-u",userName);
        map.put("-d",passWd);
        return map;
    }

    public static void importFile2Oracle(String tableName,String file,String geoClo) {
        Map<String,String> map = new HashMap<>();
        map = iniAndGet();
        String argsStr = new String();
        for(Map.Entry s:map.entrySet()){
            argsStr += s.getKey()+","+s.getValue()+",";
        }
        argsStr += "-t"+","+tableName+",";
        argsStr += "-f,"+file+",";
        argsStr += "-g,"+geoClo;
        String[] args = argsStr.split(",");
        SampleShapefileToJGeomFeature geo = new SampleShapefileToJGeomFeature();
        try{
            geo.main(args);
            System.out.println("导入成功！");
        }catch(Exception e){
            System.out.println("导入异常！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        importFile2Oracle("region1","F:\\KingWang\\论文\\实验数据\\测试区域1\\region1","shape");
    }

    public static String getIpFrom(String url){
        String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else{
            return "0.0.0.0";
        }
    }

}

