package com.project.tutor.controller;

import com.project.tutor.many.dto.RoleManyDTO;
import com.project.tutor.request.RoleRequest;
import com.project.tutor.respone.ResponeData;
import com.project.tutor.service.RoleService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    public static ResponeData data  = new ResponeData();

    @Autowired
    RoleService roleService;

    @GetMapping
    private ResponseEntity<?> getAllRole (){
        return new ResponseEntity<>(roleService.getAllRole(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById (@PathVariable int id){
        RoleManyDTO roleMany = roleService.getRoleById(id);
        return new ResponseEntity<>(roleMany,HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRole (@RequestBody RoleRequest request){
        RoleRequest roleRequest = roleService.addRole(request);
        if(roleRequest != null){
            data.setData(true);
            data.setMsg("Add role success");
        }else{
            data.setData(false);
            data.setMsg("Add role fail!");
        }
        return new ResponseEntity<>(data,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRoleById (@PathVariable int id){
        boolean checkDelete = roleService.deleteRole(id);

        data.setData(checkDelete ? true : false);
        data.setMsg(checkDelete ? "Delete success" : "Delete fail!");
        return new ResponseEntity<>(data,HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRoleById (@PathVariable int id , @RequestBody RoleRequest request){
        boolean checkUpdate = roleService.updateRole(id,request);

        data.setData(checkUpdate ? true : false);
        data.setMsg(checkUpdate ? "Update role success" : "Update role fail!");
        return new ResponseEntity<>(data,HttpStatus.OK);
    }
}
