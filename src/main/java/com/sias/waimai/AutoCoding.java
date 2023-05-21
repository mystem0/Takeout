package com.sias.waimai;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;

public class AutoCoding {
        public static void main(String[] args) {
            //new一个代码生成器对象
            AutoGenerator autoGenerator = new AutoGenerator();

            //数据库设置
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
            dataSourceConfig.setUrl("jdbc:mysql:///reggie?serverTimezone=UTC");
            dataSourceConfig.setUsername("root");
            dataSourceConfig.setPassword("root");
            autoGenerator.setDataSource(dataSourceConfig);//提交给代码生成器
            //全局配置
            GlobalConfig globalConfig = new GlobalConfig();
            globalConfig.setOutputDir(System.getProperty("user.dir") + "/src/main/java");//指定代码生成位置
            globalConfig.setOpen(false);//代码成功生成后是否在文件夹中打开
            globalConfig.setAuthor("li");//作者
            globalConfig.setFileOverride(false);//覆盖上次生成的文件
            globalConfig.setMapperName("%sMapper");//设置数据层接口名称,“%s”为占位符，创建接口时自动替换成实体类名称进行拼接
            globalConfig.setIdType(IdType.ASSIGN_ID);//id生成策略
            autoGenerator.setGlobalConfig(globalConfig);//提交
            //设置包名
            PackageConfig packageConfig = new PackageConfig();
            packageConfig.setParent("com.sias.waimai");//在全局配置的路径下创建总包
            packageConfig.setEntity("pojo");//创建实体类包
            packageConfig.setMapper("mapper");//创建数据层包
            autoGenerator.setPackageInfo(packageConfig);//提交
            //策略配置
            StrategyConfig strategyConfig = new StrategyConfig();
            strategyConfig.setInclude("dish_flavor");//表名
            strategyConfig.setTablePrefix();//设置数据库表的前缀名称，模块名 = 数据库表名 - 前缀名 例如： User = tbl_user - tbl_
            strategyConfig.setRestControllerStyle(true);//是否启用Rest风格
            strategyConfig.setVersionFieldName("version");//设置乐观锁字段名
            strategyConfig.setLogicDeleteFieldName("deleted");//设置逻辑删除字段名
            strategyConfig.setEntityLombokModel(true);//设置是否启用lombok
            autoGenerator.setStrategy(strategyConfig);

            //执行生成操作
            autoGenerator.execute();

        }

}
