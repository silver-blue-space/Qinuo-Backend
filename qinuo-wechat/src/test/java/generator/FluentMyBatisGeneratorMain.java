package generator;

import cn.hutool.json.JSONObject;
import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Column;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import com.qinuo.common.typehandler.ArrayLongTypeHandler;
import com.qinuo.common.typehandler.JsonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.test4j.module.database.proxy.DataSourceCreator;

public class FluentMyBatisGeneratorMain {
    static final String url = "jdbc:mysql://localhost:3306/qinuo?useSSL=false&useUnicode=true&characterEncoding=utf-8";

    /**
     * 使用 test/resource/init.sql文件自动初始化测试数据库
     */
    @BeforeClass
    public static void runDbScript() {
        DataSourceCreator.create("dataSource");
    }

    @Test
    public void generate() {
        FileGenerator.build(Abc.class);
    }

    @Tables(
            /* 数据库连接信息 **/
            url = url, username = "root", password = "root",
            /* Entity类parent package路径 **/
            basePack = "com.qinuo",
            /* Entity代码源目录 **/
            srcDir = "src/main/java",
            /* Dao代码源目录 **/
            daoDir = "src/main/java",
            /* 如果表定义记录创建，记录修改，逻辑删除字段 **/
            gmtCreated = "gmt_created", gmtModified = "gmt_modified", logicDeleted = "is_deleted",
            /* 需要生成文件的表 ( 表名称:对应的Entity名称 ) **/
            tables ={
                    @Table(value = "wx_user", columns = @Column(value = "tagid_list", javaType = String.class)),
                    @Table(
                    value = {
                            "qn_scheduling"
                    }),
            }
    )
    static class Abc {
    }
}