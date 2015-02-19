package com.example;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.dynamicreports.jasper.builder.export.Exporters;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.component.RectangleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.ImageScale;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

public class SampleReport {
	private Connection connection;

	public SampleReport() {
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://NI-NTB-064:5432/modelpos",
					"postgres", "postgres");
			build();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void build() {
		StyleBuilder style = stl.style().setVerticalAlignment(VerticalAlignment.MIDDLE).setRadius(10)
				.setBackgroundColor(new Color(109, 158, 235))
				.setLinePen(stl.pen().setLineColor(Color.LIGHT_GRAY));
		RectangleBuilder background = cmp.rectangle().setStyle(style)
				.setPrintWhenExpression(exp.printInOddRow());

		try {
			report().setColumnStyle(Templates.columnStyle)
					.setColumnTitleStyle(Templates.boldCenteredStyle)
					.setColumnHeaderBackgroundComponent(background)
					.setDetailBackgroundComponent(background)
					.columns(
							col.column("Id", "id", type.integerType()).setHorizontalAlignment(
									HorizontalAlignment.CENTER),
							col.column("Name", "name", type.stringType()).setHorizontalAlignment(
									HorizontalAlignment.CENTER),
							col.column("Id Number", "id_number", type.stringType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER))
					.title(cmp.verticalList().add(
							cmp.image("src/resources/logo.png")
									.setHorizontalAlignment(HorizontalAlignment.CENTER).setDimension(50, 50),
							Components
									.text("Clients")
									.setStyle(
											Templates.bold18CenteredStyle.setForegroundColor(
													new Color(109, 158, 235)).setBackgroundColor(
													new Color(255, 255, 255)))
									.setHorizontalAlignment(HorizontalAlignment.CENTER)
									.setHeight(35))).pageFooter(Templates.footerComponent)
					.setDataSource("SELECT id, name, id_number FROM client", connection).show();
			// .toPdf(Exporters.pdfExporter(new File("asdasd.pdf")));
		} catch (DRException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new SampleReport();
	}
}