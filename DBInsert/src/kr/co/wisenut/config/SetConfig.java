package kr.co.wisenut.config;

import kr.co.wisenut.Exception.ConfigException;
import kr.co.wisenut.Exception.StringException;
import kr.co.wisenut.config.datasource.GetDataSource;
import kr.co.wisenut.config.source.GetSource;
import kr.co.wisenut.logger.Log2;

/**
 *
 * SetConfig
 *
 * xml정보를 set하는 클래스
 *
 * @author  이준명
 *
 */

public class SetConfig {
    private Config m_config = new Config();

    public Config getConfig(RunTimeArgs rta) throws ConfigException, StringException{

        //new GetSource(config path, config srcid);
        GetSource source = new GetSource(rta.getConf(), rta.getSrcid());

        m_config.setSource( source.getSource() );
        m_config.setCollection(source.getCollection());

        Log2.out("[info] [SetConfig] [XML path : " + rta.getConf() + "]");
        m_config.setDataSource(new GetDataSource(rta.getConf()).getDataSource());

        return m_config;
    }
}
