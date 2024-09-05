package org.geekhub.coursework.delivery.main.repository.restaurant;

import org.geekhub.coursework.delivery.main.model.MenuItem;
import org.springframework.jdbc.core.RowMapper;

public class MenuItemMapper {
    private MenuItemMapper(){
    }

    static RowMapper<MenuItem> getMenuRowMapper() {
        return (rs, rowNum) -> {
            MenuItem item = new MenuItem();
            item.setId(rs.getLong("id"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getBigDecimal("price"));
            item.setWeight(rs.getInt("weight"));
            item.setServing(rs.getInt("serving"));
            item.setDescription(rs.getString("description"));

            return item;
        };
    }
}
