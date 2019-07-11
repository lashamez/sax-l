<!-- ***** Contact Area Start ***** -->
<section class="contact-area section_padding_100">
    <div class="container">
        <div class="row">
            <!-- Contact Form Area -->
            <div class="col-12 col-md-8">
                <div class="contact-form-area">
                    <h2>Get in touch</h2>
                    <form action="MessageToAdminServlet" method="post">
                        <div class="row">
                            <div class="col-12 col-md-6">
                                <input type="text" class="form-control" required name="name" id="name" placeholder="Name">
                            </div>
                            <div class="col-12 col-md-6">
                                <input type="email" class="form-control" required name="mail" id="email" placeholder="E-mail">
                            </div>
                            <div class="col-12">
                                <input type="text" class="form-control" required name="subject" id="subject" placeholder="Subject">
                            </div>
                            <div class="col-12">
                                <textarea name="message" class="form-control" required name="message" id="message" cols="30" rows="10" placeholder="Message"></textarea>
                            </div>
                            <button class="btn mosh-btn mt-50" type="submit">Send Message</button>
                        </div>
                    </form>
                </div>
            </div>
            <!-- Contact Information -->
            <div class="col-12 col-md-4">
                <div class="contact-information">
                    <h2>Contact</h2>
                    <div class="single-contact-info d-flex">
                        <div class="contact-icon mr-15">
                            <img src="${pageContext.request.contextPath}/img/core-img/message.png" alt="">
                        </div>
                        <p>saxl.service@gmail.com</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<!-- ***** Contact Area End ***** -->