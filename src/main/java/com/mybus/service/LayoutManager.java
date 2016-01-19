package com.mybus.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.mybus.dao.LayoutDAO;
import com.mybus.dao.impl.LayoutMongoDAO;
import com.mybus.model.Layout;
import com.mybus.model.LayoutType;
import com.mybus.model.Row;
import com.mybus.model.Seat;

/**
 * Created by schanda on 01/15/16.
 */
@Service
public class LayoutManager {
	
    private static final Logger logger = LoggerFactory.getLogger(LayoutManager.class);

    public static final int SEMI_SLEEPER_DEFAULT_LEFT_ROWS = 2;
    
    public static final int SEMI_SLEEPER_DEFAULT_RIGHT_ROWS = 2;
    
    public static final int SEMI_SLEEPER_DEFAULT_COLUMNS = 11;
    
    @Autowired
    private LayoutMongoDAO layoutMongoDAO;

    @Autowired
    private LayoutDAO layoutDAO;

    public boolean deleteLayout(String name) {
        Preconditions.checkNotNull(name, "The layout name can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Deleting layout :[{}]" + name);
        }
        if (layoutDAO.findOne(name) != null) {
            layoutDAO.delete(name);
        } else {
            throw new RuntimeException("Unknown layout name");
        }
        return true;
    }

    public Layout saveLayout(Layout layout) {
        Preconditions.checkNotNull(layout, "The layout can not be null");
        Preconditions.checkNotNull(layout.getName(), "The layout name can not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Saving layout :[{}]" + layout);
        }
        return layoutDAO.save(layout);
    }
    
    public Layout getDefaultLayout(LayoutType layoutType){
    	System.out.println(" preparing default layout for " + layoutType);
    	Layout layout = null;
    	if (LayoutType.SLEEPER.equals(layoutType)){
    		layout = constructSleeperLayout();
    	} else {
    		System.out.println(" layout type :" + layoutType);
    		layout = constructSemiSleeperLayout();
    	}
    	System.out.println(" layout object :" + layout);
    	return layout;
    }

    /**
     * Default layout for Semi-sleeper
     */
    private Layout constructSemiSleeperLayout(){
    	System.out.println(" constructing layout object ");
    	Layout layout = new Layout();
    	layout.setActive(true);
    	layout.setType(LayoutType.AC_SEMI_SLEEPER);
    	char c = 'D';
    	List<Row> rows = new ArrayList<Row>();
    	
    	System.out.println(" constructing Right side ");
    	// Right side rows..
    	for (int i = 0; i < SEMI_SLEEPER_DEFAULT_RIGHT_ROWS; i++) {
    		Row row = new Row();
    		row.setMiddleRow(false);
    		List<Seat> seats = new ArrayList<Seat>();
			for (int j = 0, k = i; j < SEMI_SLEEPER_DEFAULT_COLUMNS; j++, k += 1) {
				System.out.println(" constructing right seat ");
				Seat seat = new Seat();
				seat.setActive(true);
				seat.setDisplay(true);
				seat.setDisplayName(j==0? String.valueOf(c--) : "R" + k++);
				seat.setWindow(i==0);
				seats.add(seat);
			}
			row.setSeats(seats);
			rows.add(row);
		}
    	
    	System.out.println(" constructing Middle ");
    	// Middle row..
		rows.add(constructMiddleRow());
		
		System.out.println(" constructing Left side ");
		// Left side rows..
    	for (int i = 0; i < SEMI_SLEEPER_DEFAULT_LEFT_ROWS; i++) {
    		Row row = new Row();
    		row.setMiddleRow(false);
    		List<Seat> seats = new ArrayList<Seat>();
			for (int j = 0, k = SEMI_SLEEPER_DEFAULT_LEFT_ROWS-i; j < SEMI_SLEEPER_DEFAULT_COLUMNS; j++){
				System.out.println(" constructing left seat ");
				Seat seat = new Seat();
				seat.setActive(!(j == 0 && i == 0));
				seat.setDisplay(!(j == 0 && i == 0));
				seat.setDisplayName(j==0? String.valueOf(c--) : "L" + k);
				k += j == 0 ? 0 : 2; 
				seat.setWindow(i+1==SEMI_SLEEPER_DEFAULT_LEFT_ROWS);
				seats.add(seat);
			}
			row.setSeats(seats);
			rows.add(row);
		}
    	
    	layout.setRows(rows);
    	return layout;
    }
    
    private Row constructMiddleRow() {
    	Row middleRow = new Row();
    	middleRow.setMiddleRow(true);
		List<Seat> seats = new ArrayList<Seat>();
    	for (int j = 1; j < SEMI_SLEEPER_DEFAULT_COLUMNS; j++) {
			Seat seat = new Seat();
			seat.setActive(false);
			seat.setDisplay(false);
			seat.setDisplayName("");
			seats.add(seat);
		}
    	Seat seat = new Seat();
		seat.setActive(true);
		seat.setDisplay(true);
		seat.setDisplayName(String.format("M%s",SEMI_SLEEPER_DEFAULT_COLUMNS * 2 - 1));
		seats.add(seat);
		middleRow.setSeats(seats);
		return middleRow;
    }
    
    
    /**
     * Default layout for Sleeper
     */
    private Layout constructSleeperLayout(){
    	return null;
    } 
    
    
}
