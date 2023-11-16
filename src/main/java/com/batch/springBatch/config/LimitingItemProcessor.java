package com.batch.springBatch.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.extensions.excel.Sheet;
import org.springframework.batch.item.ItemProcessor;


import com.batch.springBatch.entity.Insurance;



public class LimitingItemProcessor implements ItemProcessor<Insurance, Insurance> {

	 private static final int MAX_RECORDS = 20;
	    private int count = 0;
	    
	    @Override
	    public Insurance process(Insurance item) throws Exception {
	        if (count < MAX_RECORDS) {
	            count++;
	            System.out.println("Processing Record: " + item.toString());
	           
	            return item;
	        } else {
	            return null; 
	        }
	    }


	
}
