package com.example.innfrared;

/**
 * Created by Lenovo on 2019/4/3.
 */

public class Api {
    //.检查采集器是否合法http://47.88.18.157:18014
    public  static  String doDataloggerCheck="http://47.88.18.157:18014/v00009/epc/plantDevice/doDataloggerCheck.json?companyId=1&";

    //采集器下连的电表
    public  static  String getAmmetersByDatalogerSn="http://47.88.18.157:18014/v00009/epc/device/ammeter/getAmmetersByDatalogerSn.json?sn=";
    //获取电表的详细信息
    public  static  String doDetail="http://47.88.18.157:18014/v00009/epc/device/ammeter/doDetail.json?uid=1&companyId=1&deviceId=";
    //绑定采集器和电表
    public  static  String saveCommandAmmeter="http://47.88.18.157:18014/v00009/epc/device/ammeter/saveCommandAmmeter.json?uid=1&";

}
