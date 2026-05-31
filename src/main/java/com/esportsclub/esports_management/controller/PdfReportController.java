package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.service.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PdfReportController {

    private final MatchService matchService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final UserService userService;

    public PdfReportController(MatchService matchService,
                               TeamService teamService,
                               TournamentService tournamentService,
                               UserService userService) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
        this.userService = userService;
    }

    // ─── HEADER / FOOTER EVENT ───────────────────────────────────────────────
    static class HeaderFooterEvent extends PdfPageEventHelper {
        private final String generatedDate;

        HeaderFooterEvent(String generatedDate) {
            this.generatedDate = generatedDate;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            BaseColor orange = new BaseColor(255, 112, 32);
            BaseColor dark = new BaseColor(26, 26, 46);
            BaseColor lightGray = new BaseColor(220, 220, 220);

            float pageWidth = document.getPageSize().getWidth();
            float marginLeft = document.leftMargin();
            float marginRight = document.rightMargin();
            float usableWidth = pageWidth - marginLeft - marginRight;

            // ── HEADER ──
            // Turuncu çizgi
            cb.setColorFill(orange);
            cb.rectangle(marginLeft, document.getPageSize().getHeight() - 38, usableWidth, 3);
            cb.fill();

            // E ikonu (küçük kare)
            cb.setColorFill(orange);
            cb.roundRectangle(marginLeft, document.getPageSize().getHeight() - 68, 22, 22, 4);
            cb.fill();

            // E harfi
            try {
                cb.beginText();
                cb.setColorFill(BaseColor.WHITE);
                cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, false), 13);
                cb.setTextMatrix(marginLeft + 6, document.getPageSize().getHeight() - 61);
                cb.showText("E");
                cb.endText();

                // Başlık
                cb.beginText();
                cb.setColorFill(dark);
                cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, false), 11);
                cb.setTextMatrix(marginLeft + 28, document.getPageSize().getHeight() - 57);
                cb.showText("ESports Club Management System");
                cb.endText();

                // Sağda tarih
                cb.beginText();
                cb.setColorFill(new BaseColor(150, 150, 150));
                cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 8);
                cb.setTextMatrix(pageWidth - marginRight - 160, document.getPageSize().getHeight() - 57);
                cb.showText("Generated: " + generatedDate);
                cb.endText();
            } catch (Exception e) {
                // ignore
            }

            // Header alt çizgi
            cb.setColorFill(lightGray);
            cb.rectangle(marginLeft, document.getPageSize().getHeight() - 73, usableWidth, 1);
            cb.fill();

            // ── FOOTER ──
            // Footer üst çizgi
            cb.setColorFill(lightGray);
            cb.rectangle(marginLeft, 42, usableWidth, 1);
            cb.fill();

            // Turuncu vurgu çizgi
            cb.setColorFill(orange);
            cb.rectangle(marginLeft, 38, 40, 2);
            cb.fill();

            try {
                // Sol — sistem adı
                cb.beginText();
                cb.setColorFill(new BaseColor(150, 150, 150));
                cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 8);
                cb.setTextMatrix(marginLeft, 26);
                cb.showText("E-Sports Club Management System — Analytics Report");
                cb.endText();

                // Sağ — sayfa numarası
                cb.beginText();
                cb.setColorFill(new BaseColor(150, 150, 150));
                cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, false), 8);
                String pageNum = "Page " + writer.getPageNumber();
                cb.setTextMatrix(pageWidth - marginRight - 30, 26);
                cb.showText(pageNum);
                cb.endText();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    // ─── BÖLÜM BAŞLIĞI YARDIMCISI ────────────────────────────────────────────
    private void addSectionTitle(Document doc, String text, Font font) throws DocumentException {
        // Boşluk
        doc.add(Chunk.NEWLINE);

        // Başlık paragrafı
        Paragraph p = new Paragraph(text, font);
        p.setSpacingBefore(6);
        p.setSpacingAfter(4);
        doc.add(p);

        // Turuncu çizgi
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.setSpacingAfter(10);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBackgroundColor(new BaseColor(255, 112, 32));
        lineCell.setFixedHeight(2f);
        lineCell.setBorder(Rectangle.NO_BORDER);
        line.addCell(lineCell);
        doc.add(line);
    }

    @GetMapping("/reports/pdf")
    public void downloadPdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=esports-report.pdf");

        Document doc = new Document(PageSize.A4, 40, 40, 85, 60);
        PdfWriter writer = PdfWriter.getInstance(doc, response.getOutputStream());

        String generatedDate = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm").format(new java.util.Date());
        writer.setPageEvent(new HeaderFooterEvent(generatedDate));

        doc.open();

        BaseColor orange = new BaseColor(255, 112, 32);
        BaseColor dark = new BaseColor(26, 26, 46);
        BaseColor gray = new BaseColor(100, 100, 100);
        BaseColor lightGray = new BaseColor(240, 240, 245);

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, orange);
        Font subFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, gray);
        Font headFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, dark);
        Font tableHeadFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        Font tableCellFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, dark);
        Font tableCellBold = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, dark);

        // ── BAŞLIK ──
        Paragraph title = new Paragraph("E-Sports Club Management System", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        doc.add(title);

        Paragraph subtitle = new Paragraph("Analytics Report", subFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        doc.add(subtitle);

        // ── VERİ HAZIRLIĞI ──
        java.util.List<Match> allMatches = matchService.getAllMatches();
        java.util.List<Team> allTeams = teamService.getAllTeams();

        Map<Integer, String> teamNameMap = new HashMap<>();
        for (Team t : allTeams) teamNameMap.put(t.getId(), t.getName());

        Map<Integer, String> tournamentNameMap = new HashMap<>();
        tournamentService.getAllTournaments().forEach(t -> tournamentNameMap.put(t.getId(), t.getName()));

        // ── SİSTEM İSTATİSTİKLERİ ──
        addSectionTitle(doc, "System Statistics", headFont);

        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingAfter(16);

        String[] statLabels = {"Total Matches", "Total Users", "Total Tournaments", "Total Teams"};
        String[] statValues = {
                String.valueOf(allMatches.size()),
                String.valueOf(userService.getAllUsers().size()),
                String.valueOf(tournamentService.getAllTournaments().size()),
                String.valueOf(allTeams.size())
        };
        BaseColor[] statColors = {
                new BaseColor(255, 112, 32),
                new BaseColor(121, 134, 203),
                new BaseColor(76, 175, 80),
                new BaseColor(38, 166, 154)
        };

        for (int i = 0; i < statLabels.length; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(12);
            cell.setBackgroundColor(statColors[i]);

            Paragraph valP = new Paragraph(statValues[i],
                    new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.WHITE));
            valP.setAlignment(Element.ALIGN_CENTER);

            Paragraph lblP = new Paragraph(statLabels[i],
                    new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, new BaseColor(255, 255, 255, 200)));
            lblP.setAlignment(Element.ALIGN_CENTER);

            cell.addElement(valP);
            cell.addElement(lblP);
            statsTable.addCell(cell);
        }
        doc.add(statsTable);

        // ── MVP TAKIM ──
        Map<Integer, Long> winCounts = new HashMap<>();
        for (Match m : allMatches) {
            if (m.getWinnerId() != null && m.getWinnerId() > 0) {
                winCounts.merge(m.getWinnerId(), 1L, Long::sum);
            }
        }

        addSectionTitle(doc, "MVP Team", headFont);

        if (!winCounts.isEmpty()) {
            int mvpId = Collections.max(winCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            long mvpWins = winCounts.get(mvpId);
            String mvpName = teamNameMap.getOrDefault(mvpId, "Team #" + mvpId);

            PdfPTable mvpTable = new PdfPTable(2);
            mvpTable.setWidthPercentage(60);
            mvpTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            mvpTable.setSpacingAfter(16);

            // Header row
            PdfPCell nameLabel = new PdfPCell(new Phrase("Team Name", tableHeadFont));
            nameLabel.setBackgroundColor(orange);
            nameLabel.setPadding(8);
            nameLabel.setBorderColor(BaseColor.WHITE);
            mvpTable.addCell(nameLabel);

            PdfPCell winsLabel = new PdfPCell(new Phrase("Total Wins", tableHeadFont));
            winsLabel.setBackgroundColor(orange);
            winsLabel.setPadding(8);
            winsLabel.setBorderColor(BaseColor.WHITE);
            mvpTable.addCell(winsLabel);

            // Data row
            PdfPCell nameVal = new PdfPCell(new Phrase(mvpName, tableCellBold));
            nameVal.setPadding(8);
            nameVal.setBackgroundColor(lightGray);
            mvpTable.addCell(nameVal);

            PdfPCell winsVal = new PdfPCell(new Phrase(String.valueOf(mvpWins),
                    new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, orange)));
            winsVal.setPadding(8);
            winsVal.setBackgroundColor(lightGray);
            mvpTable.addCell(winsVal);

            doc.add(mvpTable);
        } else {
            Paragraph noMvp = new Paragraph("No match results available yet.", tableCellFont);
            noMvp.setSpacingAfter(16);
            doc.add(noMvp);
        }

        // ── TÜM MAÇLAR ──
        addSectionTitle(doc, "All Matches", headFont);

        PdfPTable matchTable = new PdfPTable(6);
        matchTable.setWidthPercentage(100);
        matchTable.setWidths(new float[]{2f, 2f, 1.5f, 2f, 2f, 1.5f});
        matchTable.setSpacingAfter(16);

        // Header
        String[] matchHeaders = {"Tournament", "Team 1", "Score", "Team 2", "Date", "Status"};
        for (String h : matchHeaders) {
            PdfPCell cell = new PdfPCell(new Phrase(h, tableHeadFont));
            cell.setBackgroundColor(dark);
            cell.setPadding(7);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(new BaseColor(50, 50, 70));
            matchTable.addCell(cell);
        }

        // Rows
        boolean altRow = false;
        for (Match m : allMatches) {
            BaseColor rowColor = altRow ? lightGray : BaseColor.WHITE;
            altRow = !altRow;

            String tournamentName = m.getTournamentId() != null
                    ? tournamentNameMap.getOrDefault(m.getTournamentId(), "ID: " + m.getTournamentId()) : "-";
            String team1Name = m.getTeam1Id() != null
                    ? teamNameMap.getOrDefault(m.getTeam1Id(), "Team #" + m.getTeam1Id()) : "-";
            String team2Name = m.getTeam2Id() != null
                    ? teamNameMap.getOrDefault(m.getTeam2Id(), "Team #" + m.getTeam2Id()) : "-";
            String score = (m.getTeam1Score() != null ? m.getTeam1Score() : 0)
                    + " - " + (m.getTeam2Score() != null ? m.getTeam2Score() : 0);
            String date = m.getMatchDate() != null ? m.getMatchDate().toString() : "-";
            String status = m.getStatus() != null ? m.getStatus() : "-";

            // Status rengi
            BaseColor statusColor = dark;
            if ("FINISHED".equalsIgnoreCase(status)) statusColor = new BaseColor(76, 175, 80);
            else if ("ONGOING".equalsIgnoreCase(status)) statusColor = new BaseColor(255, 152, 0);
            else if ("PENDING".equalsIgnoreCase(status)) statusColor = new BaseColor(121, 134, 203);

            String[][] cells = {
                    {tournamentName, null},
                    {team1Name, null},
                    {score, null},
                    {team2Name, null},
                    {date, null},
                    {status, String.valueOf(statusColor.getRed() + "," + statusColor.getGreen() + "," + statusColor.getBlue())}
            };

            for (int i = 0; i < cells.length; i++) {
                Font cellFont = i == 5
                        ? new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, statusColor)
                        : tableCellFont;
                PdfPCell cell = new PdfPCell(new Phrase(cells[i][0], cellFont));
                cell.setBackgroundColor(rowColor);
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderColor(new BaseColor(220, 220, 220));
                matchTable.addCell(cell);
            }
        }

        if (allMatches.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("No matches found.", tableCellFont));
            emptyCell.setColspan(6);
            emptyCell.setPadding(10);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            matchTable.addCell(emptyCell);
        }

        doc.add(matchTable);
        doc.close();
    }
}