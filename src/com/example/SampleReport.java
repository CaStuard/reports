package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.Exporters;
import net.sf.dynamicreports.jasper.builder.export.JasperPdfExporterBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.Columns;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

public class SampleReport {

	public static void main(String[] args) throws FileNotFoundException {
		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb",
					"postgres", "postgres");
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		JasperReportBuilder report = DynamicReports.report();
		// a new report
		report.columns(Columns.column("Id", "id", DataTypes.integerType()),
				Columns.column("Name", "name", DataTypes.stringType()))
				.title(
				// title of the report
				Components.text("On Boarding Advance Report")
						.setHorizontalAlignment(HorizontalAlignment.CENTER))
				.pageFooter(Components.pageXofY())// show page number on the
													// page footer
				.setDataSource("SELECT id, name FROM PERSON", connection);

		try {
			// show the report
//			report.show();
			report.toPdf(Exporters.pdfExporter(new File("asdasd.pdf")));
		} catch (DRException e) {
			e.printStackTrace();
		}
	}
}
