package com.techelevator.dao;

import com.techelevator.model.Park;
import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcReservationDao implements ReservationDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcReservationDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String sql = "insert into reservation (site_id, name, from_date, to_date) " +
                "values (?, ?, ?, ?) returning reservation_id;";
        return jdbcTemplate.queryForObject(sql, Integer.class, siteId, name, fromDate, toDate);
    }

    public List<Reservation> getUpcomingReservations(Park park) {
        List<Reservation> upcomingReservations = new ArrayList<>();
        String sql = "select reservation_id, site_id, reservation.name, from_date, to_date, create_date " +
                "from reservation " +
                "join site using(site_id) " +
                "join campground using(campground_id) " +
                "join park using(park_id) " +
                "where park_id = ? " +
                "and from_date <= ((select current_date) + 30) " +
                "and from_date >= (select current_date);";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, park.getParkId());

        while (results.next()) {
            Reservation reservation = mapRowToReservation(results);
            upcomingReservations.add(reservation);
        }

        return upcomingReservations;
    }

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
