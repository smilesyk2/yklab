package kr.co.wisenut.config;
import java.util.HashMap;

import kr.co.wisenut.config.catalogInfo.Mapping;
import kr.co.wisenut.config.source.Source;

/**
 *
 * Config
 *
 * xml의 정보를 저장하는 클래스
 *
 * @author 이준명
 *
 */
public class Config {
    private Mapping m_mapping = null;
  //  private
    private Source source = null;
    private HashMap dataSource;


    public void setDataSource(HashMap dataSource){
        this.dataSource = dataSource;
    }

    public HashMap getDataSource(){
        return dataSource;
    }

    public void setCollection(Mapping mapping){
        m_mapping = mapping;
    }

    public Mapping getCollection(){
        return m_mapping;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    protected void debug(String msg){
        System.out.println(msg);
    }
}
