package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.dao.contactRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private contactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME: " + userName);

		User user = userRepository.getUserByUserName(userName);

		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String bashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact form

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage")MultipartFile file, Principal principal, HttpSession session) {
		
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			//Processing and uploading file....
			
			if(file.isEmpty())
			{
				System.out.println("File is empty");
				contact.setImage("contact.png");
			}
			else
			{
				contact.setImage(file.getOriginalFilename());
				File imageFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(imageFile.getAbsolutePath()+File.separator+file.getOriginalFilename());			
				Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
				
			}
			
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			//success message
			session.setAttribute("message", new Message("Your contact is added","success"));
			
			
		} catch (Exception e) {
			
			session.setAttribute("message", new Message("Something went wrong","danger"));
		}
		return "normal/add_contact_form";
	}
	
	
	//show contacts handler
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")Integer page, Model m,Principal principal)
	{
		m.addAttribute("title","Show user contacts");
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 8);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	//showing specific contact details
	
	@RequestMapping("/{cID}/contact")
	public String showContactDetail(@PathVariable("cID")Integer cID, Model model,Principal principal)
	{
		Optional<Contact> contactOptional = this.contactRepository.findById(cID);
		Contact contact = contactOptional.get();
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId() == contact.getUser().getId())
		{
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		return"normal/contact_detail";
	}
	
	//delete contact
	@GetMapping("/delete/{cID}")
	@Transactional
	public String deleteContact(@PathVariable("cID")Integer cID, Model model,Principal principal, HttpSession session)
	{
		Optional<Contact> contactOptional = this.contactRepository.findById(cID);
		Contact contact = contactOptional.get();
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId() == contact.getUser().getId())
		{
			this.contactRepository.delete(contact);
			session.setAttribute("message", new Message("contact deleted successfully","success"));
		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	//open update form handler
	@PostMapping("/update-contact/{cID}")
	public String updateForm(@PathVariable("cID")Integer cID, Model model)
	{
		model.addAttribute("title", "update contact");
		
		Contact contact = this.contactRepository.findById(cID).get();
		model.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	//update contact handler
	@RequestMapping(value = "/process-update",method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage")MultipartFile file, Model m, HttpSession session,Principal principal)
	{
		

		try {
				Contact oldcontactDetail = this.contactRepository.findById(contact.getcID()).get();
			
			if(!file.isEmpty())
			{
				//delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile,oldcontactDetail.getImage());
				file1.delete();
				
				//update new photo
				File imageFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(imageFile.getAbsolutePath()+File.separator+file.getOriginalFilename());			
				Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}
			else {
				contact.setImage(oldcontactDetail.getImage());
			}
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated...","success"));

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getcID()+"/contact";
	}
	
	//Your Profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model m)
	{
		m.addAttribute("title", "profile page");
		return"normal/profile";
	}
	

}
