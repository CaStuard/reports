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
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

public class SampleReport {
	private Connection connection;

	public SampleReport() {
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://NI-NTB-010:5432/onboarding",
					"postgres", "postgres");
			build();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void build() {
		StyleBuilder style = stl.style().setVerticalAlignment(VerticalAlignment.MIDDLE)
				.setRadius(10).setBackgroundColor(new Color(226, 228, 255))
				.setLinePen(stl.pen().setLineColor(Color.WHITE));
		RectangleBuilder background = cmp.rectangle().setStyle(style)
				.setPrintWhenExpression(exp.printInOddRow());

		try {
			report().setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
					.setColumnStyle(Templates.columnStyle)
					.setColumnTitleStyle(Templates.boldCenteredStyle)
					.setColumnHeaderBackgroundComponent(background)
					.setDetailBackgroundComponent(background)
					.columns(
							col.column("Participant Name", "participantname", type.stringType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Program Description", "programdescription",
									type.stringType()).setHorizontalAlignment(
									HorizontalAlignment.CENTER),
							col.column("Start Date", "startdate", type.stringType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Status", "programstatus", type.stringType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Asigned Tasks", "asignedtasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Started Tasks", "startedtasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Tasks in Progress", "inprogresstasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Completed Tasks", "completedtasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER),
							col.column("Total Tasks", "totaltasks", type.integerType())
									.setHorizontalAlignment(HorizontalAlignment.CENTER))
					.title(cmp.verticalList().add(
							cmp.image("src/resources/logo.png")
									.setHorizontalAlignment(HorizontalAlignment.CENTER)
									.setDimension(50, 50),
							Components
									.text("On-boarding Progress Status Report")
									.setStyle(
											Templates.bold18CenteredStyle.setForegroundColor(
													new Color(58, 148, 210)).setBackgroundColor(
													new Color(255, 255, 255)))
									.setHorizontalAlignment(HorizontalAlignment.CENTER)
									.setHeight(35)))
					.pageFooter(Templates.footerComponent)
					.setDataSource(
							"select concat(participant.name, ' ', participant.lastname) as participantname, program.description as programdescription, to_char(program.started, 'yyyy/mm/dd') as startdate, program.status as programstatus, (select count(task.id) from task where task.status = 'ASIGNED' and task.program_id = program.id) as asignedtasks, (select count(task.id) from task where task.status = 'STARTED' and task.program_id = program.id) as startedtasks, (select count(task.id) from task where task.status = 'IN_PROGRESS' and task.program_id = program.id) as inprogresstasks, (select count(task.id) from task where task.status = 'COMPLETED' and task.program_id = program.id) as completedtasks, count(task.id) as totaltasks from participant join program on participant.id = program.participant_id left join task on program.id = task.program_id group by participantname, programdescription, programstatus, startdate, program.id",
							connection).show();
			// .toPdf(Exporters.pdfExporter(new File("asdasd.pdf")));
		} catch (DRException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new SampleReport();
	}
}