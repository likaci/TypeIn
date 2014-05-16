package com.xiazhiri.TypeIn;

/**
* Created by Administrator on 5.16 016.
*/
class ProcessItem {
    String type;
    String status;
    Object data;
    String discription;

    public ProcessItem(String type, Object data, String discription) {
        this.type = type;
        this.data = data;
        this.status = "未上传";
        this.discription = discription;
    }

}
