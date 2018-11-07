package com.mybus.dao.impl;

import com.mybus.dao.LayoutDAO;
import com.mybus.model.Layout;
import com.mybus.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by schanda on 1/16/16.
 */
@Repository
public class LayoutMongoDAO {

	@Autowired
	private LayoutDAO layoutDAO;

	public Layout save(Layout layout) {
		return layoutDAO.save(layout);
	}

	public Layout update(Layout layout) throws Exception {
		Layout dbCopy = layoutDAO.findById(layout.getId()).get();
		dbCopy.merge(layout);
		return layoutDAO.save(dbCopy);
	}

}
