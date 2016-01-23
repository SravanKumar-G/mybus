package com.mybus.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mybus.dao.LayoutDAO;
import com.mybus.model.Layout;
import com.mybus.service.SessionManager;

/**
 * Created by schanda on 1/16/16.
 */
@Repository
public class LayoutMongoDAO {

	@Autowired
	private LayoutDAO layoutDAO;

	@Autowired
	private SessionManager sessionManager;

	public Layout save(Layout layout) {
		return layoutDAO.save(layout);
	}

	public Layout update(Layout layout) throws Exception {
		Layout dbCopy = layoutDAO.findOneByName(layout.getName());
		dbCopy.merge(layout);
		return layoutDAO.save(dbCopy);
	}

}
