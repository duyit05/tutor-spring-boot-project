package com.project.tutor.controller;

import com.project.tutor.dto.PaymentResDTO;
import com.project.tutor.request.PaymentRequest;
import com.project.tutor.respone.ResponeDataAuth;
import com.project.tutor.respone.ResponseData;
import com.project.tutor.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {
    public static ResponseData data = new ResponseData();
    @Autowired
    PaymentService paymentService;

    @GetMapping("/list")
    public ResponseEntity<?> getAllListPayment (){
        return new ResponseEntity<>(paymentService.getAllPayment(), HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addPayment (@RequestBody PaymentRequest request){
        boolean checkAddPayment = paymentService.addPayment(request);
        data.setData(checkAddPayment ? true : false);
        data.setMsg(checkAddPayment ? "Add payment success" : "Add payment fail!");
        return new ResponseEntity<>(data , HttpStatus.OK);
    }

    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity<?> deletePaymentById (@PathVariable int paymentId){
        boolean checkDeletePayment = paymentService.deletePayment(paymentId);
        data.setData(checkDeletePayment ? true : false);
        data.setMsg(checkDeletePayment ? "Delete payment success" : "Delete payment fail!");
        return new ResponseEntity<>(data , HttpStatus.OK);
    }

    @PutMapping("/update/{paymentId}")
    public ResponseEntity<?> updatePaymentById (@PathVariable int paymentId , @RequestBody PaymentRequest request){
        boolean checkUpdatePayment = paymentService.updatePayment(paymentId , request);
        data.setData(checkUpdatePayment ? true : false);
        data.setMsg(checkUpdatePayment ? "Update payment success" : "Update payment fail!");
        return new ResponseEntity<>(data , HttpStatus.OK);
    }

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment (HttpServletRequest req, @RequestBody PaymentRequest paymentRequest){
        PaymentResDTO payment = paymentService.createPayment(paymentRequest , req);
        return new ResponseEntity<>(payment , HttpStatus.OK);
    }
}
