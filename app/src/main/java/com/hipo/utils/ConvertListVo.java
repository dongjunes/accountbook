package com.hipo.utils;

import com.hipo.pojo.AddedListVo;
import com.hipo.pojo.ListVo;

/**
 * Created by dongjune on 2017-05-02.
 */

public class ConvertListVo {

    public static ListVo converting(AddedListVo addedListVo) {
        ListVo listVo = new ListVo();
        listVo.setName(addedListVo.getName());
        listVo.setCategory(addedListVo.getCategory());
        listVo.setPaid(addedListVo.getPaid());
        listVo.setMoney(AddedListVoFunction.convertMoney(addedListVo.getMoney()));
        listVo.setBank(addedListVo.getBank());
        listVo.setDay(addedListVo.getDay());
        listVo.setId(addedListVo.getId());
        listVo.setListId(addedListVo.getListId());
        listVo.setLocationX(addedListVo.getLocationX());
        listVo.setLocationY(addedListVo.getLocationY());
        listVo.setOperations(addedListVo.getOperations());
        return listVo;
    }


}
