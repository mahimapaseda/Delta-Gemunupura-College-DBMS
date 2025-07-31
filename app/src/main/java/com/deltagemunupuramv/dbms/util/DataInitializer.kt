package com.deltagemunupuramv.dbms.util

import android.util.Log
import com.deltagemunupuramv.dbms.model.Asset
import com.deltagemunupuramv.dbms.model.AssetCategory
import com.deltagemunupuramv.dbms.model.AssetStatus
import com.deltagemunupuramv.dbms.model.AssetType
import com.deltagemunupuramv.dbms.model.Exam
import com.deltagemunupuramv.dbms.model.ExamStatus
import com.deltagemunupuramv.dbms.model.ExamType
import com.deltagemunupuramv.dbms.model.Staff
import com.deltagemunupuramv.dbms.model.StaffStatus
import com.deltagemunupuramv.dbms.model.StaffType
import com.deltagemunupuramv.dbms.model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object DataInitializer {
    private const val TAG = "DataInitializer"
    
    private val firestore = FirebaseFirestore.getInstance()
    private val realtimeDb = FirebaseDatabase.getInstance()
    
    /**
     * Create sample admin user for testing
     */
    suspend fun createSampleUsers(): Boolean {
        return try {
            val sampleUsers = listOf(
                User(
                    uid = "admin001",
                    fullName = "System Administrator",
                    username = "admin",
                    email = "admin@deltagemunupura.lk",
                    role = AccessLevel.ROLE_ADMINISTRATOR,
                    createdAt = System.currentTimeMillis()
                ),
                User(
                    uid = "principal001",
                    fullName = "School Principal",
                    username = "principal",
                    email = "principal@deltagemunupura.lk",
                    role = AccessLevel.ROLE_PRINCIPAL,
                    createdAt = System.currentTimeMillis()
                ),
                User(
                    uid = "dataoffice001",
                    fullName = "Data Officer",
                    username = "dataoffice",
                    email = "dataoffice@deltagemunupura.lk",
                    role = AccessLevel.ROLE_DATA_OFFICER,
                    createdAt = System.currentTimeMillis()
                )
            )
            
            sampleUsers.forEach { user ->
                realtimeDb.reference
                    .child("users")
                    .child(user.uid)
                    .setValue(user)
                    .await()
            }
            
            Log.d(TAG, "Sample users created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sample users", e)
            false
        }
    }
    
    /**
     * Create sample staff for testing (stored as users in Realtime Database)
     */
    suspend fun createSampleStaff(): Boolean {
        return try {
            val sampleStaff = listOf(
                // Principal - Full Access
                Staff(
                    id = "staff001",
                    fullName = "Dr. Samantha Perera",
                    nameWithInitials = "Dr. S. Perera",
                    nicNumber = "197512345678",
                    registrationNumber = "P001",
                    email = "principal@deltagemunupura.lk",
                    password = "principal123",
                    phoneNumber = "0771234567",
                    personalAddress = "123 Principal Lane, Colombo",
                    dateOfBirth = "1975-05-15",
                    gender = "Female",
                    maritalStatus = "Married",
                    spouseName = "Dr. Rohan Perera",
                    dateOfFirstAppointment = "2000-01-15",
                    dateOfAppointmentToSchool = "2015-03-01",
                    appointedSubject = "Principal",
                    educationalQualifications = listOf("B.Ed", "M.Ed", "Ph.D Educational Leadership"),
                    professionalQualifications = listOf("Educational Leadership Certificate", "School Management Diploma"),
                    emergencyContactName = "Dr. Rohan Perera",
                    emergencyContactPhone = "0779876543",
                    staffType = StaffType.ACADEMIC,
                    status = StaffStatus.ACTIVE
                ),
                
                // Data Officer - Full Access
                Staff(
                    id = "staff002",
                    fullName = "Nimal Kumar Silva",
                    nameWithInitials = "N.K. Silva",
                    nicNumber = "198506789012",
                    registrationNumber = "DO001",
                    email = "dataoffice@deltagemunupura.lk",
                    password = "dataoffice123",
                    phoneNumber = "0712345678",
                    personalAddress = "456 Data Street, Gampaha",
                    dateOfBirth = "1985-08-22",
                    gender = "Male",
                    maritalStatus = "Single",
                    dateOfFirstAppointment = "2010-07-01",
                    dateOfAppointmentToSchool = "2010-07-01",
                    appointedSubject = "Data Officer",
                    educationalQualifications = listOf("B.Sc. Computer Science", "M.Sc. Information Systems"),
                    professionalQualifications = listOf("Database Management Certificate", "Data Analytics Diploma"),
                    emergencyContactName = "Kamala Silva",
                    emergencyContactPhone = "0754321098",
                    staffType = StaffType.NON_ACADEMIC,
                    status = StaffStatus.ACTIVE
                ),
                
                // Technical Officer - Full Access
                Staff(
                    id = "staff003",
                    fullName = "Priya Rajesh Fernando",
                    nameWithInitials = "P.R. Fernando",
                    nicNumber = "197803456789",
                    registrationNumber = "TO001",
                    email = "technical@deltagemunupura.lk",
                    password = "technical123",
                    phoneNumber = "0765432109",
                    personalAddress = "789 Tech Lane, Kandy",
                    dateOfBirth = "1978-12-10",
                    gender = "Female",
                    maritalStatus = "Married",
                    spouseName = "Rajesh Fernando",
                    dateOfFirstAppointment = "2005-03-15",
                    dateOfAppointmentToSchool = "2005-03-15",
                    appointedSubject = "Technical Officer",
                    educationalQualifications = listOf("B.Sc. Information Technology", "M.Sc. Network Engineering"),
                    professionalQualifications = listOf("Cisco Certified", "Microsoft Certified Professional"),
                    emergencyContactName = "Rajesh Fernando",
                    emergencyContactPhone = "0776543210",
                    staffType = StaffType.NON_ACADEMIC,
                    status = StaffStatus.ACTIVE
                ),
                
                // Academic Staff - Partial Access
                Staff(
                    id = "staff004",
                    fullName = "John David Smith",
                    nameWithInitials = "J.D. Smith",
                    nicNumber = "199012345678",
                    registrationNumber = "T001",
                    email = "john.smith@deltagemunupura.lk",
                    password = "teacher123",
                    phoneNumber = "0771234567",
                    personalAddress = "123 Teacher St, Colombo",
                    dateOfBirth = "1990-05-15",
                    gender = "Male",
                    maritalStatus = "Married",
                    spouseName = "Jane Smith",
                    dateOfFirstAppointment = "2015-01-15",
                    dateOfAppointmentToSchool = "2018-03-01",
                    appointedSubject = "Mathematics",
                    subjectsTaught = listOf("Mathematics", "Additional Mathematics"),
                    gradesTaught = listOf("10", "11", "12", "13"),
                    educationalQualifications = listOf("B.Sc. Mathematics", "PGDE"),
                    emergencyContactName = "Jane Smith",
                    emergencyContactPhone = "0779876543",
                    staffType = StaffType.ACADEMIC,
                    status = StaffStatus.ACTIVE
                ),
                
                // Non-Academic Staff - Partial Access
                Staff(
                    id = "staff005",
                    fullName = "Mary Elizabeth Wilson",
                    nameWithInitials = "M.E. Wilson",
                    nicNumber = "198506789012",
                    registrationNumber = "S001",
                    email = "mary.wilson@deltagemunupura.lk",
                    password = "support123",
                    phoneNumber = "0712345678",
                    personalAddress = "456 Support Road, Gampaha",
                    dateOfBirth = "1985-08-22",
                    gender = "Female",
                    maritalStatus = "Single",
                    dateOfFirstAppointment = "2010-07-01",
                    dateOfAppointmentToSchool = "2010-07-01",
                    appointedSubject = "Administrative Support",
                    professionalQualifications = listOf("Diploma in Office Management", "Certificate in Customer Service"),
                    emergencyContactName = "Robert Wilson",
                    emergencyContactPhone = "0754321098",
                    staffType = StaffType.NON_ACADEMIC,
                    status = StaffStatus.ACTIVE
                )
            )
            
            sampleStaff.forEach { staff ->
                // Save staff to staff collection for management (like students)
                realtimeDb.reference
                    .child("staff")
                    .child(staff.id)
                    .setValue(staff)
                    .await()
                
                // Also save to users collection for sign-in authentication
                val user = com.deltagemunupuramv.dbms.util.StaffUserConverter.staffToUser(staff)
                realtimeDb.reference
                    .child("users")
                    .child(staff.id)
                    .setValue(user)
                    .await()
            }
            
            Log.d(TAG, "Sample staff created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sample staff", e)
            false
        }
    }
    
    /**
     * Create sample assets for testing
     */
    suspend fun createSampleAssets(): Boolean {
        return try {
            val sampleAssets = listOf(
                Asset(
                    id = "asset001",
                    name = "Computer Lab Desktop 01",
                    type = AssetType.EQUIPMENT.name,
                    category = AssetCategory.IT_EQUIPMENT.name,
                    location = "Computer Lab 1",
                    status = AssetStatus.IN_USE.name,
                    purchaseDate = "2023-01-15",
                    purchasePrice = 45000.0,
                    currentValue = 40000.0,
                    assignedTo = "Computer Lab Teacher",
                    assignedDepartment = "IT Department",
                    serialNumber = "PC001-2023",
                    manufacturer = "Dell",
                    model = "OptiPlex 7090",
                    warrantyExpiry = "2026-01-15",
                    lastMaintenance = "2024-01-15",
                    nextMaintenance = "2024-07-15",
                    description = "Desktop computer for student use in computer lab",
                    notes = "Regular maintenance required",
                    bookName = "",
                    itemNumber = "PC001",
                    item = "Desktop Computer",
                    dateEntered = "2023-01-15",
                    voucherNumber = "VOUCH001",
                    fromWhomReceived = "Dell Technologies",
                    dateRemoved = "",
                    reasonForRemoval = "",
                    other = ""
                ),
                
                Asset(
                    id = "asset002",
                    name = "Science Lab Microscope Set",
                    type = AssetType.EQUIPMENT.name,
                    category = AssetCategory.LABORATORY.name,
                    location = "Science Lab 2",
                    status = AssetStatus.AVAILABLE.name,
                    purchaseDate = "2022-08-20",
                    purchasePrice = 25000.0,
                    currentValue = 22000.0,
                    assignedTo = "",
                    assignedDepartment = "Science Department",
                    serialNumber = "MICRO-2022-001",
                    manufacturer = "Olympus",
                    model = "CX23",
                    warrantyExpiry = "2025-08-20",
                    lastMaintenance = "2024-02-20",
                    nextMaintenance = "2024-08-20",
                    description = "Set of 10 microscopes for biology classes",
                    notes = "Handle with care, fragile equipment",
                    bookName = "",
                    itemNumber = "MICRO002",
                    item = "Microscope Set",
                    dateEntered = "2022-08-20",
                    voucherNumber = "VOUCH002",
                    fromWhomReceived = "Olympus Corporation",
                    dateRemoved = "",
                    reasonForRemoval = "",
                    other = "Set of 10 microscopes"
                ),
                
                Asset(
                    id = "asset003",
                    name = "Library Books - Mathematics Collection",
                    type = AssetType.BOOKS.name,
                    category = AssetCategory.LIBRARY.name,
                    location = "Main Library",
                    status = AssetStatus.AVAILABLE.name,
                    purchaseDate = "2023-03-10",
                    purchasePrice = 15000.0,
                    currentValue = 14000.0,
                    assignedTo = "Librarian",
                    assignedDepartment = "Library",
                    serialNumber = "LIB-MATH-2023",
                    manufacturer = "Various Publishers",
                    model = "Mathematics Textbooks",
                    warrantyExpiry = "",
                    lastMaintenance = "",
                    nextMaintenance = "",
                    description = "Collection of 50 mathematics textbooks for grades 6-13",
                    notes = "Regular inventory check required",
                    bookName = "Mathematics Textbooks Collection",
                    itemNumber = "LIB003",
                    item = "Book Collection",
                    dateEntered = "2023-03-10",
                    voucherNumber = "VOUCH003",
                    fromWhomReceived = "Book Suppliers Ltd",
                    dateRemoved = "",
                    reasonForRemoval = "",
                    other = "50 mathematics textbooks for grades 6-13"
                ),
                
                Asset(
                    id = "asset004",
                    name = "School Bus - Route 1",
                    type = AssetType.VEHICLE.name,
                    category = AssetCategory.TRANSPORT.name,
                    location = "School Parking",
                    status = AssetStatus.IN_USE.name,
                    purchaseDate = "2021-06-15",
                    purchasePrice = 2500000.0,
                    currentValue = 2000000.0,
                    assignedTo = "Bus Driver - Mr. Silva",
                    assignedDepartment = "Transport Department",
                    serialNumber = "BUS-2021-001",
                    manufacturer = "Tata",
                    model = "LPO 1623",
                    warrantyExpiry = "2024-06-15",
                    lastMaintenance = "2024-01-15",
                    nextMaintenance = "2024-04-15",
                    description = "School bus for student transportation on Route 1",
                    notes = "Regular service and maintenance required",
                    bookName = "",
                    itemNumber = "BUS004",
                    item = "School Bus",
                    dateEntered = "2021-06-15",
                    voucherNumber = "VOUCH004",
                    fromWhomReceived = "Tata Motors",
                    dateRemoved = "",
                    reasonForRemoval = "",
                    other = "45-seater school bus for Route 1"
                ),
                
                Asset(
                    id = "asset005",
                    name = "Administrative Office Furniture Set",
                    type = AssetType.FURNITURE.name,
                    category = AssetCategory.ADMINISTRATIVE.name,
                    location = "Admin Office",
                    status = AssetStatus.IN_USE.name,
                    purchaseDate = "2022-11-05",
                    purchasePrice = 75000.0,
                    currentValue = 65000.0,
                    assignedTo = "Administrative Staff",
                    assignedDepartment = "Administration",
                    serialNumber = "FURN-ADMIN-2022",
                    manufacturer = "Office Furniture Co.",
                    model = "Executive Suite",
                    warrantyExpiry = "2025-11-05",
                    lastMaintenance = "",
                    nextMaintenance = "",
                    description = "Complete office furniture set including desks, chairs, and cabinets",
                    notes = "Good condition, regular cleaning required",
                    bookName = "",
                    itemNumber = "FURN005",
                    item = "Furniture Set",
                    dateEntered = "2022-11-05",
                    voucherNumber = "VOUCH005",
                    fromWhomReceived = "Office Furniture Co.",
                    dateRemoved = "",
                    reasonForRemoval = "",
                    other = "Complete office furniture set with desks, chairs, and cabinets"
                )
            )
            
            sampleAssets.forEach { asset ->
                realtimeDb.reference
                    .child("assets")
                    .child(asset.id)
                    .setValue(asset)
                    .await()
            }
            
            Log.d(TAG, "Sample assets created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sample assets", e)
            false
        }
    }
    
    /**
     * Create sample exams for testing
     */
    suspend fun createSampleExams(): Boolean {
        return try {
            val sampleExams = listOf(
                Exam(
                    id = "exam001",
                    examType = ExamType.TERM_TEST,
                    title = "Mathematics Term Test 1",
                    description = "First term test for Grade 10 Mathematics",
                    examDate = "2024-03-15",
                    startTime = "09:00",
                    endTime = "11:00",
                    duration = "2 hours",
                    grade = "Grade 10",
                    subject = "Mathematics",
                    totalMarks = 100,
                    passMarks = 40,
                    venue = "Main Hall",
                    invigilator = "Mr. John Smith",
                    status = ExamStatus.SCHEDULED
                ),
                
                Exam(
                    id = "exam002",
                    examType = ExamType.A_L_RESULTS,
                    title = "A/L Results 2023",
                    description = "Advanced Level Examination Results for 2023",
                    examDate = "2024-01-15",
                    startTime = "",
                    endTime = "",
                    duration = "",
                    grade = "Grade 13",
                    subject = "All Subjects",
                    totalMarks = 0,
                    passMarks = 0,
                    venue = "School Office",
                    invigilator = "",
                    status = ExamStatus.COMPLETED,
                    examYear = "2023",
                    examMonth = "August",
                    resultsPublished = true,
                    resultsDate = "2024-01-15"
                ),
                
                Exam(
                    id = "exam005",
                    examType = ExamType.TERM_TEST,
                    title = "English Literature Test",
                    description = "Term test for Grade 12 English Literature",
                    examDate = "2024-03-20",
                    startTime = "14:00",
                    endTime = "16:00",
                    duration = "2 hours",
                    grade = "Grade 12",
                    subject = "English",
                    totalMarks = 80,
                    passMarks = 32,
                    venue = "Classroom 12A",
                    invigilator = "Ms. Mary Wilson",
                    status = ExamStatus.SCHEDULED
                )
            )
            
            sampleExams.forEach { exam ->
                realtimeDb.reference
                    .child("exams")
                    .child(exam.id)
                    .setValue(exam)
                    .await()
            }
            
            Log.d(TAG, "Sample exams created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sample exams", e)
            false
        }
    }

    /**
     * Create sample O/L exams organized by year
     */
    suspend fun createSampleOLExams(): Boolean {
        return try {
            val sampleOLExams = mapOf(
                "2023" to listOf(
                    Exam(
                        id = "ol_2023_001",
                        examType = ExamType.O_L_RESULTS,
                        title = "O/L Results 2023",
                        description = "Ordinary Level Examination Results for 2023",
                        examDate = "2024-01-10",
                        startTime = "",
                        endTime = "",
                        duration = "",
                        grade = "Grade 11",
                        subject = "All Subjects",
                        totalMarks = 0,
                        passMarks = 0,
                        venue = "School Office",
                        invigilator = "",
                        status = ExamStatus.COMPLETED,
                        examYear = "2023",
                        examMonth = "December",
                        resultsPublished = true,
                        resultsDate = "2024-01-10",
                        // O/L specific fields
                        indexNo = "123456",
                        fullName = "Kumara Perera",
                        nicNo = "200512345678",
                        attemptNo = "1st Attempt",
                        gender = "Male",
                        medium = "Sinhala",
                        religion = "A",
                        languageLiterature = "A",
                        english = "B",
                        science = "A",
                        mathematics = "A",
                        history = "B",
                        firstSubjectGroup = "A",
                        secondSubjectGroup = "B",
                        thirdSubjectGroup = "C"
                    ),
                    
                    Exam(
                        id = "ol_2023_002",
                        examType = ExamType.O_L_RESULTS,
                        title = "O/L Results 2023",
                        description = "Ordinary Level Examination Results for 2023",
                        examDate = "2024-01-10",
                        startTime = "",
                        endTime = "",
                        duration = "",
                        grade = "Grade 11",
                        subject = "All Subjects",
                        totalMarks = 0,
                        passMarks = 0,
                        venue = "School Office",
                        invigilator = "",
                        status = ExamStatus.COMPLETED,
                        examYear = "2023",
                        examMonth = "December",
                        resultsPublished = true,
                        resultsDate = "2024-01-10",
                        // O/L specific fields
                        indexNo = "123457",
                        fullName = "Nimali Silva",
                        nicNo = "200612345679",
                        attemptNo = "1st Attempt",
                        gender = "Female",
                        medium = "English",
                        religion = "B",
                        languageLiterature = "B",
                        english = "A",
                        science = "B",
                        mathematics = "A",
                        history = "A",
                        firstSubjectGroup = "A",
                        secondSubjectGroup = "B",
                        thirdSubjectGroup = "C"
                    )
                ),
                
                "2022" to listOf(
                    Exam(
                        id = "ol_2022_001",
                        examType = ExamType.O_L_RESULTS,
                        title = "O/L Results 2022",
                        description = "Ordinary Level Examination Results for 2022",
                        examDate = "2023-01-10",
                        startTime = "",
                        endTime = "",
                        duration = "",
                        grade = "Grade 11",
                        subject = "All Subjects",
                        totalMarks = 0,
                        passMarks = 0,
                        venue = "School Office",
                        invigilator = "",
                        status = ExamStatus.COMPLETED,
                        examYear = "2022",
                        examMonth = "December",
                        resultsPublished = true,
                        resultsDate = "2023-01-10",
                        // O/L specific fields
                        indexNo = "122456",
                        fullName = "Sunil Fernando",
                        nicNo = "200412345670",
                        attemptNo = "1st Attempt",
                        gender = "Male",
                        medium = "Sinhala",
                        religion = "A",
                        languageLiterature = "A",
                        english = "A",
                        science = "A",
                        mathematics = "A",
                        history = "A",
                        firstSubjectGroup = "A",
                        secondSubjectGroup = "B",
                        thirdSubjectGroup = "C"
                    )
                )
            )
            
            sampleOLExams.forEach { (year, exams) ->
                exams.forEach { exam ->
                    realtimeDb.reference
                        .child("ol_exams")
                        .child(year)
                        .child(exam.id)
                        .setValue(exam)
                        .await()
                }
            }
            
            Log.d(TAG, "Sample O/L exams created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sample O/L exams", e)
            false
        }
    }
    
    /**
     * Initialize all sample data
     */
    suspend fun initializeSampleData(): Boolean {
        return try {
            val usersCreated = createSampleUsers()
            val staffCreated = createSampleStaff()
            val assetsCreated = createSampleAssets()
            val examsCreated = createSampleExams()
            val olExamsCreated = createSampleOLExams()
            
            val success = usersCreated && staffCreated && assetsCreated && examsCreated && olExamsCreated
            if (success) {
                Log.d(TAG, "All sample data initialized successfully")
            } else {
                Log.w(TAG, "Some sample data initialization failed")
            }
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing sample data", e)
            false
        }
    }
} 