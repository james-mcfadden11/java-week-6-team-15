package com.techelevator.dao;

import com.techelevator.model.Park;
import com.techelevator.model.Reservation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.junit.Assert.assertEquals;

public class JdbcReservationDaoTests extends BaseDaoTests {

    private ReservationDao dao;
    private JdbcReservationDao sut;

    @Before
    public void setup() {
        dao = new JdbcReservationDao(dataSource);
        sut = new JdbcReservationDao(dataSource);
    }

    @Test
    public void createReservation_Should_ReturnNewReservationId() {
        int reservationCreated = dao.createReservation(1,
                "TEST NAME",
                now().plusDays(1),
                now().plusDays(3));

        assertEquals(5, reservationCreated);
    }

    @Test
    public void getUpcomingReservations_returns_correct_upcoming_reservations_for_parkID_one() {
        Park testPark = new Park();
        testPark.setParkId(1);
        testPark.setName("test park name");

        LocalDate establishedDate = now();
        testPark.setEstablishDate(establishedDate);

        testPark.setArea(123);
        testPark.setVisitors(456);
        testPark.setDescription("test park description");

        List<Reservation> actualParkList = sut.getUpcomingReservations(testPark);

        Assert.assertEquals(2, actualParkList.size());

    }

}
