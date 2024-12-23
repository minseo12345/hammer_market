package com.hammer.hammer.admin.controller;

import com.hammer.hammer.admin.service.AdminService;
import com.hammer.hammer.category.entity.Category;
import com.hammer.hammer.transaction.dto.TransactionStatusDto;
import com.hammer.hammer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;


    //관리자페이지 이동
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("welcomeMessage","welcome admin page!");
        return "/admin/dashboard";
    }

    //사용자 조회
    @GetMapping("/users")
    public String getAllUsers(Model model){
        List<User> userList = adminService.getAllUsers();
        model.addAttribute("users",userList);
        return "/admin/users";
    }

    //경매현황 조회
    @GetMapping("/transcations")
    public String getAllTransactions(Model model) {
        List<TransactionStatusDto> transactions = adminService.getTransactionStatuses();
        model.addAttribute("transactions", transactions);
        return "/admin/transactions"; 
    }
    /*
    //카테고리 관리
	@GetMapping("/categories")
	public String getCategory(Model model) {
	List<Category> categories = adminService.findAll();
	model.addAttribute("categories", categories);
	return "/admin/categories";
}
	//특정 카테고리 선택
    @GetMapping("/categories/{id}")
    public String getCategoryById(@PathVariable Long id, Model model) {
    	if(adminService.findById(id).isPresent()) {
    		model.addAttribute("category", adminService.findById(id).get());
    	}
        return "categories/detail"; // categories/detail.html 반환
    }
    
    // 카테고리 추가 페이지
   @GetMapping("/categories/new")
   public String createCategoryForm(Model model) {
	   model.addAttribute("category",new Category());
	   return "/admin/categories/new";
   }
    // 카테고리 생성
    //@PostMapping("/categories")
    
    // 카테고리 수정 페이지
   // @GetMapping("/categories/{id}/edit")
    
 // 카테고리 수정
    //@PostMapping("/categories/{id}")
    
    
    // 카테고리 삭제
    //@PostMapping("/categories/{id}/delete")
*/
}
