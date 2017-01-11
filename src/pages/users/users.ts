import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { User } from '../../models/user';
import { GithubUsers } from '../../providers/github-users';

import { UserDetailsPage } from '../user-details/user-details';

@Component({
  selector: 'page-users',
  templateUrl: 'users.html'
})
export class UsersPage {

  users: User[];

  constructor(public navCtrl: NavController, private githubUsers: GithubUsers) {
    githubUsers.load().subscribe(users => {
      this.users = users;
    })
  }

  goToDetails(login: string) {
    this.navCtrl.push(UserDetailsPage, {login});
  }
}
