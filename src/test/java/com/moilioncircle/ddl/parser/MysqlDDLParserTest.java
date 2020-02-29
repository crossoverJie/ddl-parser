package com.moilioncircle.ddl.parser;

import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MysqlDDLParserTest {

    @Test
    public void testParse() throws Exception {
        String str = "create TABLE nb_category_extra (`category_id` int NOT NULL COMMENT 'FK(\"nb_category\")',`category_weight` int NOT NULL COMMENT '分类权重',`category_tier` int NOT NULL COMMENT '所在层数',PRIMARY KEY (`category_id`)) ENGINE=MyISAM DEFAULT CHARACTER SET=utf8 COMMENT='分类扩展表(CATE_EXTRA)';"
                + "create TABLE nb_category (`category_id` int NOT NULL COMMENT 'FK(\"nb_category\")',`category_weight` int NOT NULL COMMENT '分类权重',`category_tier` int NOT NULL COMMENT '所在层数',PRIMARY KEY (`category_id`)) ENGINE=MyISAM DEFAULT CHARACTER SET=utf8 COMMENT='分类扩展表(CATE_EXTRA)';"
                + "alter table nb_category add CONSTRAINT `a` PRIMARY KEY (`category_id_alter`) ;";
        List<TableElement> tables = new MysqlDDLParser().parse(str);
        tables.forEach(System.out::println);
        assertEquals(tables.size(), 2);
        assertEquals(tables.get(0).getTableName().getValue(), "nb_category_extra");
        assertEquals(tables.get(0).getPks().get(0).getValue(), "category_id");
        assertEquals(tables.get(0).getColumns().size(), 3);
    }

    @Test
    public void testParse1() throws Exception {
        try (Reader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("test.sql"))) {
            List<TableElement> tables = new MysqlDDLParser().parse(reader);
            tables.forEach(System.out::println);
            assertEquals(tables.size(), 4);
        }
    }


    @Test
    public void parser() throws IOException {
        String sql = "DROP TABLE IF EXISTS `delivery_fee_plan_info`;\nCREATE TABLE `delivery_fee_plan_info` (\n`id` int(11) NOT NULL AUTO_INCREMENT,\n`city_id` int(11) NOT NULL,\n`name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '名称',\n`start_step_fee` decimal(15,2) NOT NULL COMMENT '起步价',\n`free_distance` int(11) NOT NULL COMMENT '免费配送距离',\n`distance_unit` int(11) NOT NULL COMMENT '计算费用的最小距离单位',\n`is_deleted` int(11) NOT NULL DEFAULT 0 COMMENT '删除状态 0未删除 1删除',\n`create_time` datetime NOT NULL,\n`update_time` datetime NULL,\nPRIMARY KEY (`id`)\n)\nCOMMENT = '配送费方案基本信息表';\n\nDROP TABLE IF EXISTS `delivery_fee_plan_detail_normal`;\nCREATE TABLE `delivery_fee_plan_detail_normal` (\n`id` int(11) NOT NULL AUTO_INCREMENT,\n`plan_id` int(11) NOT NULL,\n`type` int(11) NOT NULL COMMENT '1(正常距离)、2(额外距离)',\n`min_distance` int(11) COMMENT '起始距离',\n`max_distance` int(11) COMMENT '截止距离',\n`fee` decimal(15,4) NOT NULL COMMENT '计费金额，与delivery_fee_plan_info中的distance_unit一起计算，比如：0.2刀每100米',\n`fixed_fee` decimal(15,2) NOT NULL DEFAULT 0 COMMENT '固定费用，默认为0',\nPRIMARY KEY (`id`) ,\nINDEX `idx_plan_id` (`plan_id` ASC) USING BTREE\n)\nCOMMENT = '普通时段运费明细（一个配送方案里普通时段有且只有一条）';";
        List<TableElement> tables = new MysqlDDLParser().parse(sql);
        assertEquals(tables.size(), 2);
    }
}