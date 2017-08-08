import { createHistory } from 'history'
import React from 'react';
import { render } from 'react-dom';
import { IndexRoute, Link, Route, Router } from 'react-router'

function sendJson(params) {
    params.data = JSON.stringify(params.data);
    params.contentType = "application/json";
    return $.ajax(params);
}

function getUser(userId) {
    return $.ajax({
        url: "/api/users/" + userId,
        type: "GET"
    }).then(
        function(user) {
            return user;
        },
        function() {
            return $.when(null);
        }
    );
}

var Chirp = React.createClass({
    render: function() {
        return (
            <div className="chirp">
                <h3 className="chirpUser">
                    <Link to={"/users/" + this.props.userId}>
                        {this.props.userName}
                    </Link>
                </h3>
                {this.props.children}
                <hr />
            </div>
        );
    }
});

var ChirpForm = React.createClass({
    getInitialState: function() {
        return {message: ""};
    },
    handleMessageChange: function(e) {
        this.setState({message: e.target.value});
    },
    handleSubmit: function(e) {
        e.preventDefault();
        var message = this.state.message.trim();
        if (!message) {
            return;
        }
    },
    render: function() {
        return (
            <form className="chirpForm" onSubmit={this.handleSubmit}>
                <input type="text"
                       placeholder="Say something..."
                       value={this.state.message}
                       onChange={this.handleMessageChange}
                       maxLength="140"
                />
                <input type="submit" value="Post" />
            </form>
        );
    }
});

var UserChirps = React.createClass({
    getInitialState: function() {
        return {notFound: false};
    },
    componentDidMount: function() {
        getUser(this.props.params.userId).then(function(user) {
            if (user) {
                this.setState({user: user});
            } else {
                this.setState({notFound: true});
            }
        }.bind(this));
    },
    render: function() {
        var userId = this.props.params.userId;
        if (this.state.notFound) {
            return (
                <div className="userChirps">
                    <h1>User {userId} not found</h1>
                </div>
            );
        } else {
            var chirpForm;
            if (userId == localStorage.userId) {
                chirpForm = <ChirpForm />
            }
            var userName;
            if (this.state.user) {
                userName = this.state.user.name;
                var users = {};
                users[userId] = this.state.user;
            } else {
                userName = userId;
            }
            return (
                <ContentLayout subtitle={"Chirps for " + userName}>
                    <Section>
                        <div className="small-12 columns">
                            {chirpForm}
                        </div>
                    </Section>
                </ContentLayout>
            );
        }
    }
});

var LoginForm = React.createClass({
    getInitialState: function() {
        return {userId: ""};
    },
    handleUserIdChange: function(e) {
        this.setState({userId: e.target.value});
    },
    handleSubmit: function(e) {
        e.preventDefault();
        var userId = this.state.userId.trim();
        if (!userId) {
            return;
        } else {
            getUser(userId).then(function (user) {
                if (user) {
                    localStorage.userId = user.userId;
                    this.props.onLogin(user);
                } else {
                    this.setState({error: "User " + userId + " does not exist."});
                }
            }.bind(this));
        }
    },
    render: function() {
        var error;
        if (this.state.error) {
            error = <Error message={this.state.error}/>;
        }
        return (
            <Section>
                <div className="small-12 large-4 columns">
                    <form className="loginForm" onSubmit={this.handleSubmit}>
                        <input type="text"
                               placeholder="Username..."
                               value={this.state.userId}
                               onChange={this.handleUserIdChange}
                        />
                        {error}
                        <input type="submit" value="Login" />
                    </form>
                </div>
            </Section>
        );
    }
});

var SignUpPage = React.createClass({
    getInitialState: function() {
        return {userId: "", name: ""};
    },
    handleUserIdChange: function(e) {
        this.setState({userId: e.target.value});
    },
    handleNameChange: function(e) {
        this.setState({name: e.target.value});
    },
    handleSubmit: function(e) {
        e.preventDefault();
        var userId = this.state.userId.trim();
        var name = this.state.name.trim();
        if (!userId || !name) {
            return;
        } else {
            var user = {userId: userId, name: name};
            sendJson({
                url: "/api/users",
                type: "POST",
                data: user
            }).then(function () {
                localStorage.userId = userId;
                this.props.history.pushState(null, "/");
            }.bind(this), function() {
                this.setState("User " + userId + " already exists.");
            }.bind(this));
        }
    },
    render: function() {
        var error;
        if (this.state.error) {
            error = <Error message={this.state.error}/>;
        }
        return (
            <PageLayout>
                <ContentLayout subtitle="Sign up">
                    <Section>
                        <div className="small-12 large-4 columns">
                            <form className="signupForm" onSubmit={this.handleSubmit}>
                                <input type="text"
                                       placeholder="Username..."
                                       value={this.state.userId}
                                       onChange={this.handleUserIdChange}
                                />
                                <input type="text"
                                       placeholder="Name..."
                                       value={this.state.name}
                                       onChange={this.handleNameChange}
                                />
                                {error}
                                <input type="submit" value="Sign up" />
                            </form>
                        </div>
                    </Section>
                </ContentLayout>
            </PageLayout>
        );
    }

});

var Error = React.createClass({
    render: function() {
        return (
            <div>
                <span className="error">{this.props.message}</span>
            </div>
        );
    }
});

var Section = React.createClass({
    render: function() {
        return (
            <section className="fw-wrapper feature">
                <div className="row">
                    {this.props.children}
                </div>
            </section>
        );
    }
});

var PageLayout = React.createClass({
    render: function() {

        var links = <div className="tertiary-nav"></div>;
        var button;

        if (this.props.user) {
            links = (
                <div className="tertiary-nav">
                    <Link to={"/users/" + this.props.user.userId }>{this.props.user.name}</Link>
                </div>
            );
            button = <a className="btn" href="#" onClick={this.props.logout}>Logout</a>;
        } else if (this.props.showSignup) {
            button = <Link className="btn" to="/signup">Sign up</Link>;
        } else {
            button = <Link className="btn" to="/">Login</Link>;
        }

        return (
             <div id="clipped">
                 <div id="site-header">
                     <div className="row">
                         <div className="small-3 columns">
                             <Link to="/" id="logo">My Lagom</Link>
                         </div>
                         <div className="small-9 columns">
                             <nav>
                                 <div className="tertiary-nav">
                                     {links}
                                 </div>
                                 <div className="primary-nav">
                                     {button}
                                 </div>
                             </nav>
                         </div>
                     </div>
                 </div>
                 {this.props.children}
            </div>
        );
    }
});

var ContentLayout = React.createClass({
   render: function() {
       return (
           <div id="page-content">
               <section id="top">
                   <div className="row">
                       <header className="large-12 columns">
                            <h1>{this.props.subtitle}</h1>
                       </header>
                   </div>
               </section>
               {this.props.children}
           </div>
       );
   }
});

var App = React.createClass({
    getInitialState: function() {
        return {loginChecked: false, user: null};
    },
    componentDidMount: function() {
        if (localStorage.userId) {
            getUser(localStorage.userId).then(function (user) {
                if (user) {
                    this.setState({loginChecked: true, user: user});
                } else {
                    localStorage.removeItem("userId");
                    this.setState({loginChecked: true});
                }
            }.bind(this));
        } else {
            this.setState({loginChecked: true});
        }
    },
    handleLogin: function(user) {
        this.setState({user: user});
    },
    logout: function(e) {
        e.preventDefault();
        localStorage.removeItem("userId");
        this.setState({user: null});
    },
    render: function() {
        if (this.state.loginChecked) {
            if (!this.state.user) {
                return (
                    <PageLayout showSignup={true}>
                        <ContentLayout subtitle="Login">
                            <LoginForm onLogin={this.handleLogin}/>
                        </ContentLayout>
                    </PageLayout>
                );
            } else {
                return (
                    <PageLayout user={this.state.user} logout={this.logout}>
                        {this.props.children}
                    </PageLayout>
                );
            }
        } else {
            return <div className="loading"></div>;
        }
    }
});

render(
    <Router history={createHistory()}>
        <Route path="/signup" component={SignUpPage}/>
        <Route path="/" component={App}>
            <Route path="/users/:userId" component={UserChirps}/>
        </Route>
    </Router>,
    document.getElementById("content")
);
