<%@ page import="java.util.Random" %>

<!-- Begin bottom banner -->
<div class="fbg">
    <div class="fbg_resize">
        <div class="col c1">
            <h2>Testimonials</h2>
            <g:set var="randNumber" value="${new Random()}" />
            <g:if test="${randNumber.nextInt()%2 == 1}">
                <g:render template="/testimonials/maryann-martone" />
            </g:if>
            <g:else>
                <g:render template="/testimonials/anita-dewaard" />
            </g:else>
        </div>
        <div class="col c2">
            <h2>Want Domeo?</h2>
            <p>If you are interested in deploying your own version of Domeo, that is possible and we  <a href="mailto:paolo.ciccarese@gmail.com">can help you witht that</a>.</p>
            <h2>Need Customization?</h2>
            <p>Domeo is an extendable platform. If you are interested in using Domeo and you need ad-hoc features, we can develop them for you.
            Please <a href="mailto:paolo.ciccarese@gmail.com">contact us</a>. </p>    
        </div>
        <div class="col c3">
            <h2>Become Alpha Tester</h2>
            <p>If you are interested in collaborating with for testing the latest features of the Domeo web toolkit, please <a href="mailto:paolo.ciccarese@gmail.com">contact us</a>. </p>

            <h2><span>Become a Developer</span></h2>
            <img src="${resource(dir: 'images/public', file: 'domeo-code.png')}" alt="Domeo Code by Paolo Ciccarese" width="262" height="101" />
            <p>Domeo is open source <!--and the code is available in GitHub  -->. If you are interested in contributing to the project 
            in coordination and with the support of our team please <a href="mailto:paolo.ciccarese@gmail.com">contact us</a>.</p>
        </div>
        <div class="clr"></div>
    </div>
</div>
<!-- End bottom banner -->