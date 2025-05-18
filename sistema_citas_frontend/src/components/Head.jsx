import { Helmet } from "react-helmet";

function Head() {
    return (
        <Helmet>
            {/* Font */}
            <link rel="preconnect" href="https://fonts.googleapis.com" />
            <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="true" />
            <link href="https://fonts.googleapis.com/css2?family=AR+One+Sans:wght@400..700&display=swap" rel="stylesheet" />

            {/* Styles */}
            <link rel="stylesheet" href="../styles.css" />

            {/* Icons */}
            <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet" />

            {/* Favicon */}
            <link rel="icon" href="../assets/doctor-patient.png" type="image/png" />

            <title>Citas Medicas</title>
        </Helmet>
    );
}

export default Head;
