function setFieldAsNotValid(){var e=document.getElementsByClassName("needs-validation");Array.prototype.filter.call(e,(function(e){e.addEventListener("submit",(function(t){!1===e.checkValidity()&&(t.preventDefault(),t.stopPropagation()),e.classList.add("was-validated")}),!1)}))}document.addEventListener("DOMContentLoaded",(function(){console.log("SIGNUP FORM");const e=window.location.search,t=new URLSearchParams(e)?.get("topics").split("-"),n=document.querySelector("#dropdownMultiselectMenuButton"),o=document.querySelector("#interestList"),r=o.querySelectorAll('input[type="checkbox"]');let s=[],i=[];t&&t.length>0&&t.forEach((e=>{i.push(e),r?.forEach((t=>{t.dataset.topicId===e&&(t.checked=!0,s.push(t.dataset.topicName)),h()})),3===t.length&&r.forEach((e=>{s.includes(e.dataset.topicName)||3!==s.length?e.disabled=!1:e.disabled=!0}))}));const a=document.querySelector("#dropdownProfessionMenuButton"),d=document.querySelector("#professionList"),l=document.querySelectorAll("#professionList li div");let c="";const m=document.querySelector("#dropdownCountryMenuButton"),u=document.querySelector("#countryList"),p=document.querySelectorAll("#countryList li div");let f="",g="",y=[],L=document.querySelector("#formComponentWrapper"),_=document.querySelector("#thankyouComponent");function h(){let e=0===s.length?"Select Area of Interest":s.join(", ");"Select Area of Interest"!==e?n.classList.add("dropdown-toggle-filled"):n.classList.remove("dropdown-toggle-filled"),n.textContent=e}function w(e,t){"block"===t.style.display?(e.classList.add("border-bottom-0"),e.classList.add("active-dropdown")):(e.classList.remove("border-bottom-0"),e.classList.remove("active-dropdown"))}L&&_&&(L.classList.add("d-block"),L.classList.remove("d-none"),_.classList.add("d-none"),_.classList.remove("d-block")),n.addEventListener("click",(function(){o.style.display="block"===o.style.display?"none":"block",w(n,o)})),document.addEventListener("click",(function(e){n.contains(e.target)||o.contains(e.target)||(o.style.display="none"),a.contains(e.target)||d.contains(e.target)||(d.style.display="none"),m.contains(e.target)||u.contains(e.target)||(u.style.display="none"),w(n,o),w(a,d),w(m,u)})),r.forEach((e=>{e.addEventListener("change",(function(){s.length<=3&&!0===e.checked&&!s.includes(e.dataset.topicName)&&(s.push(e.dataset.topicName),i.push(e.dataset.topicId)),s.length<=3&&!1===e.checked&&s.includes(e.dataset.topicName)&&(s=s.filter((t=>t!==e.dataset.topicName)),i=i.filter((t=>{e.dataset.topicId}))),r.forEach((e=>{s.includes(e.dataset.topicName)||3!==s.length?e.disabled=!1:e.disabled=!0})),h()}))})),a.addEventListener("click",(function(){d.style.display="block"===d.style.display?"none":"block",w(a,d)})),m.addEventListener("click",(function(){u.style.display="block"===u.style.display?"none":"block",w(m,u)})),l.forEach((e=>{e.addEventListener("click",(function(){c=e.textContent.trim(),d.style.display="none",w(a,d),function(){let e=""===c?"Select Profession":c,t=document.querySelector("#areaOfInterestsComponent");c===Granite.I18n.get("no_healthcare")?(t.classList.add("d-none"),t.classList.remove("d-block"),s=[],i=[]):(t.classList.add("d-block"),t.classList.remove("d-none"));"Select Profession"!==e?a.classList.add("dropdown-toggle-filled"):a.classList.remove("dropdown-toggle-filled");a.textContent=e}()}))})),p.forEach((e=>{let t=document.querySelector("#fiscalCodeInput"),n=document.querySelector("#fiscalCode");e.addEventListener("click",(function(){let o=e.getAttribute("data-country-id");g=e.getAttribute("data-country-id"),f=e.textContent.trim(),u.style.display="none",w(m,u),function(){let e=""===f?"Select Country":f;"Select Country"!==e?m.classList.add("dropdown-toggle-filled"):m.classList.remove("dropdown-toggle-filled");m.textContent=e}(),"1"===o.toLowerCase()?(t.classList.remove("d-none"),t.classList.add("d-block")):(n.value="",t.classList.add("d-none"),t.classList.remove("d-block"))}))}));let S=document.querySelector("#signUpForm");S&&S.addEventListener("submit",(async e=>{y=[],e.preventDefault();const t=new FormData(S);let n={profession:c,country:g,areasOfInterest:c!==Granite.I18n.get("no_healthcare")?i:[]};for(let[e,o]of t.entries())n={...n,[`${e}`]:o};let o={firstName:n.firstName,lastName:n.lastName,birthDate:n.dateOfBirth.replaceAll("-",""),password:n.password,email:n.email,profession:n.profession,phone:n.telNumber,country:n.country,taxIdCode:n.fiscalCode?n.fiscalCode:null,interests:n.areasOfInterest,rolesNames:[],gender:n.gender,privacyConsent:"yes"===n.privacy,profilingConsent:"yes"===n.personalDataProcessing,newsletterConsent:"yes"===n.receiveNewsletter};if(function(){let e=document.querySelector("#professionErrorString"),t=document.querySelector("#dropdownProfessionMenuButton");""===c?(y.push({id:"profession",message:Granite.I18n.get("mandatory_field")}),e.innerHTML=Granite.I18n.get("mandatory_field"),t.classList.add("cmp-signupform__borderRed"),t.classList.remove("cmp-signupform__borderGreen")):(t.classList.remove("cmp-signupform__borderRed"),t.classList.add("cmp-signupform__borderGreen"),e.innerHTML="")}(),function(){let e=document.querySelector("#countryErrorString"),t=document.querySelector("#dropdownCountryMenuButton");""===f?(y.push({id:"country",message:Granite.I18n.get("mandatory_field")}),e.innerHTML=Granite.I18n.get("mandatory_field"),t.classList.add("cmp-signupform__borderRed"),t.classList.remove("cmp-signupform__borderGreen")):(t.classList.remove("cmp-signupform__borderRed"),t.classList.add("cmp-signupform__borderGreen"),e.innerHTML="")}(),function(e){let t=document.querySelector("#interestErrorString"),n=document.querySelector("#dropdownMultiselectMenuButton");e.areasOfInterest&&0!==e.areasOfInterest.length||c===Granite.I18n.get("no_healthcare")?(n.classList.remove("cmp-signupform__borderRed"),n.classList.add("cmp-signupform__borderGreen"),t.innerHTML=""):(y.push({id:"areasOfInterest",message:Granite.I18n.get("mandatory_field")}),t.innerHTML=Granite.I18n.get("mandatory_field"),n.classList.add("cmp-signupform__borderRed"),n.classList.remove("cmp-signupform__borderGreen"))}(n),function(e){let t=document.querySelector("#passwordErrorString");e.password&&e.password!==e.passwordConfirmation||e.password&&!e.passwordConfirmation?(t.innerHTML=Granite.I18n.get("password_form_error"),y.push({id:"password",message:Granite.I18n.get("password_form_error")})):e.password||(t.innerHTML=Granite.I18n.get("mandatory_field"),y.push({id:"password",message:Granite.I18n.get("mandatory_field")}))}(n),function(e){let t=document.querySelector("#passwordConfirmationErrorString");e.passwordConfirmation&&e.passwordConfirmation!==e.password||e.passwordConfirmation&&!e.password?(t.innerHTML=Granite.I18n.get("password_form_error"),y.push({id:"passwordConfirmation",message:Granite.I18n.get("password_form_error")})):e.passwordConfirmation||(t.innerHTML=Granite.I18n.get("mandatory_field"),y.push({id:"passwordConfirmation",message:Granite.I18n.get("mandatory_field")}))}(n),function(e){let t=document.querySelector("#emailErrorString");e.email&&e.email!==e.emailConfirmation||e.email&&!e.emailConfirmation?(t.innerHTML=Granite.I18n.get("email_form_error"),y.push({id:"email",message:t.innerHTML=Granite.I18n.get("email_form_error")})):e.email||(t.innerHTML=Granite.I18n.get("mandatory_field"),y.push({id:"email",message:Granite.I18n.get("mandatory_field")}))}(n),function(e){let t=document.querySelector("#privacyErrorString");"no"!==e.privacy&&e.privacy?t.innerHTML="":(y.push({id:"privacy",message:Granite.I18n.get("accept_privacy")}),t.innerHTML="no"===e.privacy?Granite.I18n.get("accept_privacy"):Granite.I18n.get("mandatory_field"))}(n),function(e){let t=document.querySelector("#newsletterErrorString");e.personalDataProcessing?t.innerHTML="":(y.push({id:"personalDataProcessing",message:Granite.I18n.get("mandatory_field")}),t.innerHTML=Granite.I18n.get("mandatory_field"))}(n),function(e){let t=document.querySelector("#dataProcessingErrorString");e.receiveNewsletter?t.innerHTML="":(y.push({id:"receiveNewsletter",message:Granite.I18n.get("mandatory_field")}),t.innerHTML=Granite.I18n.get("mandatory_field"))}(n),function(e){let t=document.querySelector("#emailConfirmationErrorString");e.emailConfirmation&&e.emailConfirmation!==e.email||e.emailConfirmation&&!e.email?(t.innerHTML=Granite.I18n.get("email_form_error"),y.push({id:"emailConfirmation",message:Granite.I18n.get("email_form_error")})):e.emailConfirmation||(t.innerHTML=Granite.I18n.get("mandatory_field"),y.push({id:"emailConfirmation",message:Granite.I18n.get("mandatory_field")}))}(n),console.log(y),0===y.length){const e=await async function(e){let t=document.querySelector("#signupLoader"),n=document.querySelector("#ctaSignup");try{t.classList.remove("d-none"),t.classList.add("d-block"),n.disabled=!0;const o=await fetch("/libs/granite/csrf/token.json"),r=await o.json(),s=await fetch("/bin/api/awsSignUp",{method:"POST",headers:{"CSRF-Token":r.token},body:JSON.stringify(e)});return await s.json()}catch(e){console.log(e)}finally{t.classList.add("d-none"),t.classList.remove("d-block"),n.disabled=!1}}(o);e.cognitoSignUpErrorResponseDto?function(e){let t=document.querySelector("#cmp-signupform__errorsAlert");t&&e&&""!==e&&(t.classList.contains("d-none")?(t.innerHTML=e.slice(1,-1),t.classList.remove("d-none"),t.classList.add("d-block")):(t.classList.remove("d-block"),t.classList.add("d-none")))}(JSON.stringify(e.cognitoSignUpErrorResponseDto.message)):L&&_&&(L.classList.add("d-none"),L.classList.remove("d-block"),_.classList.add("d-block"),_.classList.remove("d-none"))}}))})),window.addEventListener("load",(function(){setFieldAsNotValid()}),!1);