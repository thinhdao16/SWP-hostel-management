package com.example.hostelmanage.controller;

import com.example.hostelmanage.domain.BookingDomain;
import com.example.hostelmanage.model.Account;
import com.example.hostelmanage.model.Profile;
import com.example.hostelmanage.model.Role;
import com.example.hostelmanage.repository.AccountRepository;
import com.example.hostelmanage.repository.ProfileRepository;
import com.example.hostelmanage.repository.RoleRepository;
import com.example.hostelmanage.service.AccountService;
import com.example.hostelmanage.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/account")
public class AccountController {

    @Inject
    public AccountRepository accountRepository;

    @Inject
    public ProfileRepository profileRepository;

    @Inject
    public RoleRepository roleRepository;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private JwtService jwtService;

    @Inject
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Account> createUserAccount(@RequestBody Account account) throws Exception {

        Optional<Role> role = roleRepository.findById(Long.valueOf(1));
        Role userRole= role.get();
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRoles(userRoles);
        account = accountRepository.save(account);
//        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//
//        if(!email.equals("anonymousUser")){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//
//        Optional<Account> accountChecking = accountRepository.findAccountByEmail(account.getEmail());
//
//        if(accountChecking.isPresent()){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        account.setPassword(passwordEncoder.encode(account.getPassword()));
//        account = accountRepository.save(account);


        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }


    @GetMapping("/id/{Id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long Id) throws Exception {
        Account account = accountService.getAccountById(Id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<Account>> getAllAccount(){
        Iterable<Account> list = accountRepository.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/update")
    public  ResponseEntity<?> updateAccount( @RequestBody Account account) throws Exception {

        /* There are 4 step to dynamic update a entity
        * Step 1: Find account by ID with Optional<T>
        * Step 2: Get profile ID from account which we find out before
        * Step 3: Find profile by profile ID in step 2 with Optional<T>
        * Step 4: Setter them
        * */

        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<Account> accountPresent = accountRepository.findAccountByEmail(email);
        Account thisAccount = accountPresent.get();

        thisAccount.getProfile().setCardNumber(account.getProfile().getCardNumber());
        thisAccount.getProfile().setFullName(account.getProfile().getFullName());
        thisAccount.setPhone(account.getPhone());

        accountRepository.save(thisAccount);

        return new ResponseEntity<>(thisAccount, HttpStatus.OK);

    }
    @PutMapping("/update/{Id}/role/{role_id}")
    public  ResponseEntity<?> updateRoleForAccount(
                @PathVariable Long Id,//id account
                @PathVariable Long role_id //id của role
            ) throws Exception {

        Optional<Account> acc = accountRepository.findById(Id);

        if(!acc.isPresent()) {
            throw new Exception("Account not found");
        }
        Optional<Role> role = roleRepository.findById(role_id);
        if(!role.isPresent()) {
            throw new Exception("Role not found");
        }
        Account thisAcccount = acc.get();
        Role thisRole =role.get();
        //thisAcccount.setRole(thisRole);

        /* Save them */
        accountRepository.save(thisAcccount);
        return new ResponseEntity<>(thisAcccount, HttpStatus.OK);

    }

    /* Only Test
    *  Note: We have to update status column, not use delete method!
    * */
    @DeleteMapping("/delete/{Id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long Id){
        accountRepository.deleteById(Id);
        return  new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAccountByEmail() throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Optional<Account> account = accountRepository.findAccountByEmail(email);

        if(!account.isPresent()){
            throw new Exception("Account not found");
        }

        Account thisAcccount = account.get();

        return new ResponseEntity<>(thisAcccount, HttpStatus.OK);

    }

    /* Update card number */
    @PutMapping("/updateCardNumber")
    public ResponseEntity<?> updateCardNumber(@RequestBody BookingDomain bookingDomain) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println("dwewewew" + email);
        Optional<Account> account = accountRepository.findAccountByEmail(email);
        Account thisAccount = account.get();

        Optional<Profile> profile = profileRepository.findById(thisAccount.getProfile().getId());
        Profile thisProfile = profile.get();

        thisProfile.setCardNumber(bookingDomain.getCardNumber());
        profileRepository.save(thisProfile);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }




}
