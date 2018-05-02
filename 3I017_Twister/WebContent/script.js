/* BEGIN Smthg */
/* END Smthg */

/* BEGIN Global Vars */
var conteneur;
var env;
/* END Global Vars */

/* BEGIN Objects */
function Environnement(sKey){
	this.isConnected = false;
	if (sKey=="") {
		this.sessionKey = undefined;
	} else {
		this.sessionKey = sKey;
	}
	this.isRoot = false;
	this.idUser = undefined;
	this.login = undefined;
	this.listFriends = [];
}

Environnement.prototype = {
	saveSession: function () {
		var duration = (env.isroot==true) ? 365 : 1;
		setCookie("sessionKey",this.sessionKey,duration);
		console.log(getCookie("sessionKey"));
	},
	delSession: function () {
		env.isRoot = false;
		env.idUser = undefined;
		env.login = undefined;
		setCookie("sessionKey","",-1);
		console.log(getCookie("sessionKey"));
	},
	checkSession: function () {
		var request = $.ajax({
			method: "POST",
			async: true,
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			url: "user/login",
			data: { key: this.sessionKey },
			dataType: "json",
			success: function(msg){
				console.log(msg)
				if(msg.Status=="OK"){
					//Clé valide
					env.isConnected = true;
					env.isRoot = msg.isroot;
					env.idUser = msg.user_id;
					env.login = msg.login;

					loadMainPage();
					setup();
				} else {
					//Clé invalide
					env.isConnected = false;
					env.sessionKey = undefined;
					loadConnexionPage();
				}
			}
		});
	}
};

/*
function Message(id,auteur,texte,date,comments) {
	this.id = id;
	this.auteur = auteur;
	this.texte = texte;
	this.date = date;
	if (comments==undefined) { comments=[]; }
	this.comments = comments;
}

Message.prototype = {
	getHTML: function () {
		var s = "<div id=\"message_"+this.id+"\"><div><div>"+this.auteur.getHTML()+"</div><div>"+this.date+"</div></div><div>"+this.texte+"</div><div>";

		for (var i = this.comments.length - 1; i >= 0; i--) {
			s += this.comments[i].getHTML();
		}

		s += "</div></div>";
	}
};

function Commentaire(id,auteur,texte,date){
	this.id = id;
	this.auteur = auteur;
	this.texte = texte;
	this.date = date;
}

Commentaire.prototype.getHTML = function() {
	var s = "<div id=\"commentaire_"+this.id+"\"><div><div>"+this.auteur.getHTML()+"</div><div>"+this.date+"</div></div><div>"+this.texte+"</div></div>";
};
*/

function User(id,login) {
	this.id = id;
	this.login = login;
}

User.prototype.renderHtml = function(mconteneur) {
	var template = "<div class='user-container'><div class='user link' data-id='{{iduser}}' data-login='{{loginuser}}' onclick='javascript:showProfilePage(this)'>{{loginuser}}</div></div>";
	var values = {
		iduser: this.id,
		loginuser: this.login 
	}

	var rendered = Mustache.render(template, values);
	console.log(rendered);
	mconteneur.append(rendered);
};
/* END Objects */
 
/* BEGIN Generic fct */
function init(){
	var sessionKey = getCookie("sessionKey");
	conteneur = $("#app_twister");

	if (env == undefined) {
		env = new Environnement(sessionKey);
		if (env.sessionKey != undefined)
			env.checkSession();
	}

	if (env.sessionKey == undefined && env.isConnected == false) {
		loadConnexionPage();
	}
}

function setup() {
	getListFriends(true);
}

function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires="+d.toUTCString();
	console.log(cname + "=" + cvalue + ";" + expires + ";path=/");
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

function loadConnexionPage() {
	$.get('templates/connexion.tpl.html', function(template) {
		var rendered = Mustache.render(template, {});
		conteneur.html(rendered);
	});
}

function loadMainPage() {
	$.get('templates/main.tpl.html', function(template) {
		var rendered = Mustache.render(template, {});
		conteneur.html(rendered);
	});
}

function loadMessagesList(){
	$.get('templates/message_list.tpl.html', function(template) {
		var rendered = Mustache.render(template, {});
		$('#content').html(rendered);
		getMessages($("#messages-friends"),"friend");
		getMessages($("#messages-community"));
	});
}

function loadInscriptionPage() {
	$.get('templates/inscription.tpl.html', function(template) {
		var rendered = Mustache.render(template, {});
		conteneur.html(rendered);
	});
}

function loadProfilePage(user) {
	var values = {
		userId : user.id,
		userLogin : user.login
	}
	$.get('templates/profile.tpl.html', function(template) {
		var rendered = Mustache.render(template, values);
		$('#content').html(rendered);
		getMessages($("#messages-user"),"user",-1,user.id);
	});
}

function loadSearchPage(userList,messageList) {
	var values = {
		nbRuser : userList.length,
		noUser : (userList.length>0) ? false : true,
		nbRmessage : messageList.length,
		noMessage : (messageList.length>0) ? false : true
	}
	$.get('templates/search.tpl.html', function(template) {
		var rendered = Mustache.render(template, values);
		$('#content').html(rendered);

		if (userList.length>0)
			listUser($("#users-list"),userList);

		if (messageList.length>0)
			listMessage($("#messages-list"),messageList);
	});
}

function form_erreur(form,message) {
	//var msg_box = "<div id='err_msg' class='err_msg'>"+message+"</div>";

	var msg_box = document.createElement("div");
	msg_box.id = "err_msg";
	msg_box.className = "err_msg champs";
	msg_box.innerHTML = message;

	var old_msg = $("#err_msg");

	if (old_msg.length==0) {
		form.prepend(msg_box);
	} else {
		old_msg.replaceWith(msg_box);
	}
}
/* END Generic fct */

/* BEGIN Session fct */
function userConnexion(form){
	var login = form.login.value;
	var password = form.pwd.value;
	var isRoot = form.root.checked;

	if(verifFormConnexion(form,login,password)){
		getConnexion(form,login,password,isRoot);
	}
}

function verifFormConnexion(form,login,pwd){
	if (login.length == 0) {
		form_erreur(form,"Veuillez entrer votre login");
		return false;
	}
	if (pwd.length == 0) {
		form_erreur(form,"Veuillez entrer votre mot de passe");
		return false;
	}

	return true;
}

function getConnexion(form,login,password,isRoot){
	var request = $.ajax({
		method: "POST",
		url: "user/login",
		data: { login: login, pwd: password, root: isRoot },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK" && msg.key!=undefined){
				env.sessionKey = msg.key;
				env.isConnected = true;
				env.isRoot = msg.isroot;
				env.idUser = msg.user_id;
				env.login = msg.login;

				env.saveSession();

				loadMainPage();
				setup();
			} else {
				form_erreur(form,msg.message);
			}
		}
	});
}

function userDeconnexion(){
	var request = $.ajax({
		method: "POST",
		url: "user/logout",
		data: { key: env.sessionKey },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK"){
				env.sessionKey = "";
				env.isConnected = false;
				env.delSession();
				loadConnexionPage();
			} else {
				alert("Erreur lors de la déconnexion");
			}
		}
	});
}
/* END Session fct */

/* BEGIN Inscription fct */
function userInscription(form){
	var login = form.login.value;
	var password = form.pwd.value;
	var nom = form.nom.value;
	var prenom = form.prenom.value;
	var age = form.age.value;

	if(verifFormInscription(form,login,password,nom,prenom,age)){
		createAccount(form,login,password,nom,prenom,age);
	}
}

function verifFormInscription(form,login,pwd,nom,prenom,age){
	if (login.length == 0) {
		form_erreur(form,"Veuillez entrer un login");
		return false;
	}
	if (pwd.length == 0) {
		form_erreur(form,"Veuillez entrer un mot de passe");
		return false;
	}

	return true;
}

function createAccount(form,login,password,nom,prenom,age){
	var request = $.ajax({
		method: "POST",
		url: "user/createUser",
		data: { login: login, pwd: password, nom: nom, prenom: prenom, age: age },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK"){
				alert("Compte créé avec succès. Veuillez vous connecter.");
				loadConnexionPage();
			} else {
				form_erreur(form,msg.message);
			}
		}
	});
}
/* END Inscription fct */

/* BEGIN User */
function listUser(mconteneur,users) {
	for (var i = 0; i < users.length; i++) {
		one_user = new User(users[i].id,users[i].login);

		one_user.renderHtml(mconteneur);
	}
}
/* END User */

/* BEGIN Friends fct */
function getListFriends(init=false) {
	var request = $.ajax({
		method: "POST",
		url: "friend/listFriend",
		data: { key: env.sessionKey },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK"){
				jsonFriends = msg.friends;
				for (var i = 0; i < jsonFriends.length; i++) {
					var one_friend = jsonFriends[i];
					var one_user = new User(one_friend.user_id,one_friend.login);
					env.listFriends[one_friend.user_id] = one_user;
				}

				console.log(env.listFriends);

				if(init) loadMessagesList();
			} else {
				//Unable to get friend list
			}
		}
	});
}

function changeFriendStatus(elem) {
	var id_friend = elem.getAttribute("data-user");
	if (env.listFriends[id_friend]!=undefined) {
		//Retirer ami
		var request = $.ajax({
			method: "POST",
			url: "friend/removeFriend",
			data: { key: env.sessionKey, friend: id_friend },
			dataType: "json",
			success: function(msg){
				console.log(msg)
				if(msg.Status=="OK"){
					console.log("Ami "+env.listFriends[id_friend].login+" enlevé avec succès.");
					//elem.innerHTML = "SUIVRE";
					$(".message-suivre[data-user='"+id_friend+"']").html("AJOUTER AMI");
					env.listFriends[id_friend]=undefined;
				} else {
					alert("Erreur ajout ami");
				}
			}
		});
	} else {
		//Ajouter ami
		var request = $.ajax({
			method: "POST",
			url: "friend/addFriend",
			data: { key: env.sessionKey, friend: id_friend },
			dataType: "json",
			success: function(msg){
				console.log(msg)
				if(msg.Status=="OK"){
					//elem.innerHTML = "RETIRER";
					$(".message-suivre[data-user='"+id_friend+"']").html("RETIRER AMI");
					env.listFriends[id_friend] = new User(id_friend,msg.login);
					console.log("Ami "+msg.login+" ajouté avec succès.");
				} else {
					alert("Erreur ajout ami");
				}
			}
		});
	}
}

function showProfilePage(elem) {
	u = new User(elem.getAttribute('data-id'),elem.getAttribute('data-login'));
	loadProfilePage(u);
}
/* END Friends fct */

/* BEGIN Messages fct */
/*
MessageRecovery = function (key,val) {
	console.log(val);
	if (val.comments != undefined) {
		return new Message(val.message_id,val.author,val.content,val.date,val.comments);
	} else if (val.content != undefined) {
		return new Commentaire(val.id,val.author,val.content,val.date);
	}

	if (key == "author") {
		return new Auteur(val.user_id,val.login);
	} else if (key == "date") {
		return new Date(val);
	}

	return val;
}
*/

function nouveauMessage(form) {
	var content = form.content.value;
	var parent = (form.parent!=undefined) ? form.parent.value : "";

	if(verifMessage(form,content,parent)){
		createMessage(form,content,parent);
	}
}

function verifMessage(form,content,parent) {
	return true;
}

function createMessage(form,content,parent) {
	var request = $.ajax({
		method: "POST",
		url: "message/addMessage",
		data: { key: env.sessionKey, content: content, parent: parent },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK"){
				console.log("Message créé avec succès.");
				if (parent!="") {
					mconteneur = $(form).prevAll('.listcom');
					new_message = [msg.message];
					listMessage(mconteneur,new_message);
				}
				form.content.value = "";
			} else {
				form_erreur(form,msg.message);
			}
		}
	});
}

function getOneMessage(mconteneur,messageId){
	coKey = env.sessionKey;

	var request = $.ajax({
		method: "GET",
		url: "message/getMessage",
		data: { key: coKey, id: messageId },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK"){
				listMessage(mconteneur, [msg.message]);
			} else {

			}
		}
	});
}

function getMessages(mconteneur,target="all",nbMax=-1,userId=-1){
	coKey = env.sessionKey;
	if (nbMax == -1) nbMax = "";
	if (userId == -1) userId = "";

	var d = new Date();
	var token = d.getTime();

	var request = $.ajax({
		method: "GET",
		url: "message/listMessage",
		data: { key: coKey, for: target, nb: nbMax, user: userId, token: token },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK"){
				listMessage(mconteneur, msg.messages);
			} else {

			}
		}
	});
}

function listMessage(mconteneur,messages) {
	for (var i = 0; i < messages.length; i++) {
		one_message = messages[i];

		var m_id = one_message.message_id;
		var m_auteurid = one_message.author.user_id;
		var m_auteurname = one_message.author.login;
		var m_date = one_message.date;

		if (one_message.parent_author!=undefined && one_message.parent!=undefined) {
			var m_idpauteur = one_message.parent_author.user_id;
			var m_pauteur = one_message.parent_author.login;
			var m_idpmessage = one_message.parent;
			var m_pdisplay = "";
		} else {
			var m_idpauteur = "";
			var m_pauteur = "";
			var m_idpmessage = "";
			var m_pdisplay = "display: none;";
		}

		if (m_auteurid!=env.idUser) {
			if(env.listFriends[m_auteurid]!=undefined) {
				var m_sdisplay = "";
				var m_suivitag = "RETIRER AMI";
			} else {
				var m_sdisplay = "";
				var m_suivitag = "AJOUER AMI";
			}
		} else {
			var m_sdisplay = "display: none;";
			var m_suivitag = "";
		}
		
		var m_contenu = one_message.content;

		var m_expstatus = "false";
		var m_exptag = "+";

		var m_commentaires = one_message.comments;

		var values = {
			id: m_id,
			idauteur: m_auteurid,
			auteur: m_auteurname,
			date: m_date,
			idpauteur: m_idpauteur,
			pauteur: m_pauteur,
			idpmessage: m_idpmessage,
			pdisplay: m_pdisplay,
			texte: m_contenu,
			expstatus: m_expstatus,
			exptag: m_exptag,
			sdisplay: m_sdisplay,
			suivitag: m_suivitag,
			commentaires: m_commentaires
		};

		renderMessage(mconteneur, values);
	}
}

function renderMessage(mconteneur,values){
	$.get('templates/message.tpl.html', function(template) {
		var rendered = Mustache.render(template, values);
		mconteneur.append(rendered);

		if (values.commentaires.length > 0) {
			console.log("#commentaires-"+values.id)
			listMessage(mconteneur.find("#listcom-"+values.id),values.commentaires);
		}
	});
}

function showMessageParent(elem) {
	var divopaque = $("#popup-opaque");
	var divpopup = $("#popup-message-container");
	getOneMessage(divpopup,elem.getAttribute('data-pmessage'));

	divopaque.show();
	divpopup.show();

	divopaque.click(function() {
		divpopup.hide();
		divopaque.hide();
		divpopup.html("");
	});
}

function showhide(expElem){
	var exp_node = $(expElem);
	var comments_node = exp_node.nextAll('.commentaires');
	if (exp_node.attr('data-exp')=='false') {
		//show
		exp_node.html('-');
		exp_node.attr('data-exp','true');
		comments_node.show('blind');
	} else {
		//hide
		exp_node.html('+');
		exp_node.attr('data-exp','false');
		comments_node.hide('blind');
	}
}
/* END Messages fct */

/* BEGIN Search */
function searchTwister(form) {
	var query = form.query.value

	if(verifFormSearch(form,query)){
		requestSearch(form,query);
	}
}

function verifFormSearch(form,query){
	if (query.length == 0) {
		form_erreur(form,"Veuillez entrer un ou des mots clés pour la recherche");
		return false;
	}

	return true;
}

function requestSearch(form,query){
	var request = $.ajax({
		method: "POST",
		url: "services/search",
		data: { key: env.sessionKey, q: query },
		dataType: "json",
		success: function(msg){
			console.log(msg)
			if(msg.Status=="OK"){
				loadSearchPage(msg.users,msg.messages);
			} else {
				form_erreur(form,msg.message);
			}
		}
	});
}
/* END Search */

/*
$(document).ready( function () {
	getMessages();
});
*/