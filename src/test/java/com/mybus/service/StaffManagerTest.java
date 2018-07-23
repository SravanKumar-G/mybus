package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.StaffDAO;
import com.mybus.model.Staff;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class StaffManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private StaffManager staffManager;

    @Autowired
    private StaffDAO staffDAO;

    @Before
    @After
    public void clear(){
        staffDAO.deleteAll();
    }

    @Test
    public void testCount(){
        for(int i=0;i<5;i++){
            staffDAO.save(new Staff("One"+i, "9906"+i, "aadhar"+i));
        }
        long count = staffManager.count("One1", null);
        assertEquals(count, 1);
        count = staffManager.count("99061", null);
        assertEquals(count, 1);
        count = staffManager.count("aadhar1", null);
        assertEquals(count, 1);
    }

    @Test
    public void testSearch(){
        for(int i=0;i<5;i++){
            staffDAO.save(new Staff("One"+i, "9906"+i, "aadhar"+i));
        }
        Page<Staff> staff= staffManager.findStaff("One1", null);
        assertEquals(staff.getContent().size(), 1);
        staff= staffManager.findStaff(null, null);
        assertEquals(staff.getContent().size(), 5);
    }

}