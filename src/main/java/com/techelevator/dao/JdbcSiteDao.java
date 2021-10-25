package com.techelevator.dao;

import com.techelevator.model.Campground;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> allowRV = new ArrayList<>();
        String sql = "select * from site " +
                "join campground using (campground_id) " +
                "where park_id = ? and max_rv_length > 0";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        if (results.next()) {
            allowRV.add(mapRowToSite(results));
        }


        return allowRV;
    }

    @Override
    public List<Site> getAvailableSites(int parkId) {
        List<Site> sites = new ArrayList<>();

        String sql = "select site.* from site join campground using (campground_id) where park_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);

        while(results.next()) {
            sites.add(mapRowToSite(results));
        }

        return sites;
    }

    @Override
    public List<Site> getAvailableSites(int parkId, LocalDate startDate, LocalDate endDate) {
        List<Site> currentSites = new ArrayList<>();

        String sql = "select site.* \n" +
                "from site \n" +
                "join campground using(campground_id)\n" +
                "where park_id = ?  \n" +
                "and site_id not in (select site_id from reservation where (from_date < ? and to_date > ?) or (from_date betweeen ? and ?) or (to_date between ? and ?));\n" +
                ";\n";

        SqlRowSet results = jdbcTemplate.queryForRowSet
                (
                    sql, parkId,
                    startDate, endDate,
                    startDate, endDate,
                    startDate, endDate
                );

        while(results.next()) {
            currentSites.add(mapRowToSite(results));
        }

        return currentSites;
    }



    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
