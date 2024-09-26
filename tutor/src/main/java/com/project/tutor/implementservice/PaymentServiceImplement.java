package com.project.tutor.implementservice;

import com.config.ConfigVnPay;
import com.project.tutor.dto.PaymentDTO;
import com.project.tutor.dto.PaymentResDTO;
import com.project.tutor.model.Payment;
import com.project.tutor.model.Tutor;
import com.project.tutor.repository.PaymentRepostiory;
import com.project.tutor.request.PaymentRequest;
import com.project.tutor.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PaymentServiceImplement implements PaymentService {


    PaymentRepostiory paymentRepostiory;
     ConfigVnPay configVnPay;

    @Override
    public PaymentResDTO createPayment(PaymentRequest request , HttpServletRequest req) {
        ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime nowInVietnam = now.atZone(vietnamZone);
        Date date = Date.from(nowInVietnam.toInstant());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(date);



        String orderType = "other";
        double amount = request.getPaymentPrice() * 100;
//		long amount = 100000;
        String vnp_TxnRef = configVnPay.getRandomNumber(8);
        String vnp_IpAddr = configVnPay.getIpAddress(req);
        String vnp_TmnCode = configVnPay.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", configVnPay.vnp_Version);
        vnp_Params.put("vnp_Command", configVnPay.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_CreateDate", "formattedDate");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_ReturnUrl", configVnPay.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = configVnPay.hmacSHA512(configVnPay.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = configVnPay.vnp_PayUrl + "?" + queryUrl;

        PaymentResDTO paymentResDTO = new PaymentResDTO();
        paymentResDTO.setStatus("ok");
        paymentResDTO.setMessage("Successfully");
        paymentResDTO.setURL(paymentUrl);
        System.out.println(paymentUrl);

        return paymentResDTO;
    }
    @Override
    public boolean addPayment(PaymentRequest request) {
        try {
            String paymentName = request.getPaymentName();
            LocalDateTime createAt = request.getCreateAt();
            double paymentPrice = request.getPaymentPrice();
            String paymentStatus = request.getPaymentStatus();

            Tutor tutor = new Tutor();
            tutor.setId(request.getTutorId());

            if (createAt == null) {
                createAt = LocalDateTime.now();
            }

            Payment newPayment = new Payment();
            newPayment.setPaymentName(paymentName);
            newPayment.setCreateAt(createAt);
            newPayment.setPaymentPrice(paymentPrice);
            newPayment.setPaymentStatus(paymentStatus);
            newPayment.setTutor(tutor);

            paymentRepostiory.save(newPayment);
            return true;

        } catch (Exception e) {
            throw new BadCredentialsException("Add payment fail!");
        }
    }

    @Override
    public boolean updatePayment(int paymentId, PaymentRequest request) {
        try {
            Optional<Payment> checkPaymentExistOrNot = paymentRepostiory.findById(paymentId);
            if (checkPaymentExistOrNot.isPresent()) {

                Payment payment = checkPaymentExistOrNot.get();
                payment.setPaymentName(request.getPaymentName());
                payment.setCreateAt(request.getCreateAt());
                payment.setPaymentPrice(request.getPaymentPrice());
                payment.setPaymentStatus(request.getPaymentStatus());

                paymentRepostiory.save(payment);
                return true;
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Cannot update payment with id :" + paymentId);
        }
        return false;
    }

    @Override
    public boolean deletePayment(int paymentId) {
        try {
            Optional<Payment> checkPaymentExistOrNot = paymentRepostiory.findById(paymentId);
            if (checkPaymentExistOrNot.isPresent()) {

                Payment payment = checkPaymentExistOrNot.get();
                paymentRepostiory.delete(payment);

                return true;
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Cannot delete payment with id :" + paymentId);
        }
        return false;
    }

    @Override
    public PaymentDTO getPaymentById(int paymentId) {
        try {
            Optional<Payment> checkPaymentExistOrNot = paymentRepostiory.findById(paymentId);
            if (checkPaymentExistOrNot.isPresent()) {
                Payment payment = checkPaymentExistOrNot.get();

                return PaymentDTO.builder()
                        .paymentId(payment.getId())
                        .createAt(payment.getCreateAt())
                        .paymentPrice(payment.getPaymentPrice())
                        .paymentStatus(payment.getPaymentStatus())
                        .build();
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Cannot found payment with id :" + paymentId);
        }
        return null;
    }



    @Override
    public List<PaymentDTO> getAllPayment() {
        List<Payment> listPayments = paymentRepostiory.findAll();
        List<PaymentDTO> listPaymentDTOs = new ArrayList<>();

        for (Payment payment : listPayments) {
            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .paymentId(payment.getId())
                    .createAt(payment.getCreateAt())
                    .paymentPrice(payment.getPaymentPrice())
                    .paymentStatus(payment.getPaymentStatus())
                    .build();

            listPaymentDTOs.add(paymentDTO);
        }
        return listPaymentDTOs;
    }


}
