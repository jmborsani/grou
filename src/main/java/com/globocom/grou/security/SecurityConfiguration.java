/*
 * Copyright (c) 2017-2017 Globo.com
 * All rights reserved.
 *
 * This source is subject to the Apache License, Version 2.0.
 * Please see the LICENSE file for more information.
 *
 * Authors: See AUTHORS file
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globocom.grou.security;

import com.globocom.grou.SystemEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final KeystoneAuthenticationProvider keystoneAuthenticationProvider;

    @Autowired
    public SecurityConfiguration(KeystoneAuthenticationProvider keystoneAuthenticationProvider) {
        this.keystoneAuthenticationProvider = keystoneAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (Boolean.parseBoolean(SystemEnv.DISABLE_AUTH.getValue())) {
            http.authorizeRequests().anyRequest().permitAll();
        } else {
            http.addFilterBefore(new KeystoneAuthFilter(), BasicAuthenticationFilter.class);
            http.addFilterBefore(new AuditFilter(), BasicAuthenticationFilter.class);
            http.authorizeRequests().antMatchers(HttpMethod.GET).permitAll();
            http.authorizeRequests().antMatchers(HttpMethod.HEAD).permitAll();
            http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll();
            http.authorizeRequests().antMatchers(HttpMethod.POST).fullyAuthenticated();
            http.authorizeRequests().antMatchers(HttpMethod.DELETE).fullyAuthenticated();
            http.authorizeRequests().antMatchers(HttpMethod.PUT).fullyAuthenticated();
            http.authorizeRequests().antMatchers(HttpMethod.PATCH).fullyAuthenticated();
        }
        http.csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keystoneAuthenticationProvider);
    }
}
