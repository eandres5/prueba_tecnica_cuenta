package com.bank.account.infrastructure.adapter.web.controller;

import com.bank.account.application.service.ReportService;
import com.bank.account.infrastructure.adapter.web.dto.AccountStatementDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * REST Controller for Report generation
 * Endpoint: /api/v1/reports
 * F4: Generación de reportes de estado de cuenta
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Account statement report APIs")
public class ReportController {

//    private final ReportService reportService;
//
//    /**
//     * Generates account statement in JSON format
//     * GET /api/v1/reports/{client-id}?startDate=...&endDate=...
//     */
//    @GetMapping(
//            value = "/{client-id}",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(
//            summary = "Generate account statement (JSON)",
//            description = "Generates account statement for a customer within a date range in JSON format"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Report generated successfully"),
//            @ApiResponse(responseCode = "404", description = "Customer not found"),
//            @ApiResponse(responseCode = "400", description = "Invalid date format")
//    })
//    public Flux<AccountStatementDTO> generateAccountStatementJson(
//            @Parameter(description = "Customer ID", required = true)
//            @PathVariable("client-id") Long clientId,
//
//            @Parameter(description = "Start date (format: yyyy-MM-dd'T'HH:mm:ss)", required = true)
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//
//            @Parameter(description = "End date (format: yyyy-MM-dd'T'HH:mm:ss)", required = true)
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
//
//        log.info("REST request to generate account statement for customer: {} from {} to {}",
//                clientId, startDate, endDate);
//
//        return reportService.generateAccountStatement(clientId, startDate, endDate);
//    }
//
//    /**
//     * Generates account statement in Excel format
//     * GET /api/v1/reports/{client-id}/excel?startDate=...&endDate=...
//     */
//    @GetMapping(
//            value = "/{client-id}/excel",
//            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//    )
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(
//            summary = "Generate account statement (Excel)",
//            description = "Generates account statement for a customer within a date range in Excel format"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Excel report generated successfully"),
//            @ApiResponse(responseCode = "404", description = "Customer not found"),
//            @ApiResponse(responseCode = "400", description = "Invalid date format")
//    })
//    public Mono<ResponseEntity<byte[]>> generateAccountStatementExcel(
//            @Parameter(description = "Customer ID", required = true)
//            @PathVariable("client-id") Long clientId,
//
//            @Parameter(description = "Start date (format: yyyy-MM-dd'T'HH:mm:ss)", required = true)
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//
//            @Parameter(description = "End date (format: yyyy-MM-dd'T'HH:mm:ss)", required = true)
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
//
//        log.info("REST request to generate Excel report for customer: {} from {} to {}",
//                clientId, startDate, endDate);
//
//        return reportService.generateAccountStatementExcel(clientId, startDate, endDate)
//                .map(excelBytes -> {
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.setContentType(MediaType.parseMediaType(
//                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//                    headers.setContentDispositionFormData("attachment",
//                            String.format("account_statement_%d_%s.xlsx",
//                                    clientId,
//                                    LocalDateTime.now().toString().replace(":", "-")));
//                    headers.setContentLength(excelBytes.length);
//
//                    return ResponseEntity.ok()
//                            .headers(headers)
//                            .body(excelBytes);
//                });
//    }
}