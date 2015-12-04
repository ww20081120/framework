package com.fccfc.framework.web.core.utils.excel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by wk on 2015/9/17 0013.
 */
public class ExcelExportDto<T> implements Serializable {

    private static final long serialVersionUID = 185485115408343338L;

    public ExcelExportDto(String[] fields, Map<String, String> fieldsHeader, List<T> dataList) {
        this.fields = fields;
        this.fieldsHeader = fieldsHeader;
        this.dataList = dataList;
    }

    public ExcelExportDto(String[] fields, Map<String, String> fieldsHeader, int dataSize, GetDataCallback callback) {
        this.fields = fields;
        this.fieldsHeader = fieldsHeader;
        this.countDataSize = dataSize;
        this.getDataCallback = callback;
    }

    private String[] fields;

    private Map<String, String> fieldsHeader;

    private List<T> dataList;

    private Map<String, Integer> headerColumnWidths;

    private Map<String, Converter> converterMap;

    private int countDataSize;

    private GetDataCallback getDataCallback;

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public Map<String, Integer> getHeaderColumnWidths() {
        return headerColumnWidths;
    }

    public void setHeaderColumnWidths(Map<String, Integer> headerColumnWidths) {
        this.headerColumnWidths = headerColumnWidths;
    }

    public Map<String, String> getFieldsHeader() {
        return fieldsHeader;
    }

    public void setFieldsHeader(Map<String, String> fieldsHeader) {
        this.fieldsHeader = fieldsHeader;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public Map<String, Converter> getConverterMap() {
        return converterMap;
    }

    public void setConverterMap(Map<String, Converter> converterMap) {
        this.converterMap = converterMap;
    }

    public int getCountDataSize() {
        return countDataSize;
    }

    public void setCountDataSize(int countDataSize) {
        this.countDataSize = countDataSize;
    }

    public GetDataCallback getGetDataCallback() {
        return getDataCallback;
    }

    public void setGetDataCallback(GetDataCallback getDataCallback) {
        this.getDataCallback = getDataCallback;
    }
}
