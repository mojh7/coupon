package com.mojh.cms.member.controller

import com.mojh.cms.common.ApiResponse
import com.mojh.cms.member.dto.SignupMemberRequest
import com.mojh.cms.member.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/member")
class MemberController(
    private val memberService: MemberService
) {
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@Valid @RequestBody signupMemberRequest: SignupMemberRequest): ApiResponse<*> {
        memberService.signup(signupMemberRequest)
        return ApiResponse.succeed()
    }
}