package com.hammer.hammer.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    //외부 접근하지 못하게 private

    private final AdminService adminService;


    //관리자페이지 이동
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("welcomeMessage","welcome admin page!");
        return "admin_dashboard";
    }

    //사용자 조회
    @GetMapping("/users")
    public String getAllUsers(Model model){
        List<User> userList = adminService.getAllUsers();
        model.addAttribute("users",userList);
        return "admin_users";
    }

    //경매현황 조회
    @GetMapping("/transcations")
    public String getAllTransactions(Model model) {
        List<TransactionStatusDto> transactions = adminService.getTransactionStatuses();
        model.addAttribute("transactions", transactions);
        return "admin_transactions"; //
    }

}
