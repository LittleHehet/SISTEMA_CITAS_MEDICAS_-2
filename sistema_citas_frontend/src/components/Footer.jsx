import React from 'react';

function Footer() {
    return (
        <footer className="footer">
            <div className="container">
                <div className="footer-content">
                    <p>Total Solft Inc.</p>
                    <div className="social-media">
                        <a href="https://www.instagram.com/" target="_blank" rel="noopener noreferrer">
                            <i className='bx bxl-instagram'></i>
                        </a>
                        <a href="https://www.facebook.com/" target="_blank" rel="noopener noreferrer">
                            <i className='bx bxl-facebook'></i>
                        </a>
                        <a href="https://www.tiktok.com/es/" target="_blank" rel="noopener noreferrer">
                            <i className='bx bxl-tiktok'></i>
                        </a>
                    </div>
                </div>
                <p className="copyright">
                    &copy; 2019 Tsf , Inc.
                </p>
            </div>
        </footer>
    );
}


export default Footer;
